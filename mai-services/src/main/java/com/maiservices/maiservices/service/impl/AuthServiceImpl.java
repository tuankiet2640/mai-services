package com.maiservices.maiservices.service;

import com.maiservices.maiservices.dto.UserDto;
import com.maiservices.maiservices.dto.auth.AuthResponse;
import com.maiservices.maiservices.dto.auth.LoginRequest;
import com.maiservices.maiservices.dto.auth.RefreshTokenRequest;
import com.maiservices.maiservices.dto.auth.RegisterRequest;
import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.entity.Token;
import com.maiservices.maiservices.entity.TokenType;
import com.maiservices.maiservices.entity.User;
import com.maiservices.maiservices.repository.RoleRepository;
import com.maiservices.maiservices.repository.TokenRepository;
import com.maiservices.maiservices.repository.UserRepository;
import com.maiservices.maiservices.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Get default user role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(roles)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save tokens
        saveUserToken(savedUser, accessToken, TokenType.ACCESS);
        Token refreshTokenEntity = saveUserToken(savedUser, refreshToken, TokenType.REFRESH);

        // Map user to DTO
        UserDto userDto = userMapper.toDto(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userDto)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        // Revoke all existing tokens
        revokeAllUserTokens(user);

        // Generate new tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Save tokens
        saveUserToken(user, accessToken, TokenType.ACCESS);
        saveUserToken(user, refreshToken, TokenType.REFRESH);

        // Map user to DTO
        UserDto userDto = userMapper.toDto(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userDto)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Token token = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (!token.isValid() || !jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Generate new access token
        String accessToken = jwtService.generateToken(user);

        // Save new access token
        saveUserToken(user, accessToken, TokenType.ACCESS);

        // Map user to DTO
        UserDto userDto = userMapper.toDto(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userDto)
                .build();
    }

    private Token saveUserToken(User user, String jwtToken, TokenType tokenType) {
        Instant expiryDate = jwtService.extractExpirationAsInstant(jwtToken);
        
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .expiryDate(expiryDate)
                .build();
                
        return tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
