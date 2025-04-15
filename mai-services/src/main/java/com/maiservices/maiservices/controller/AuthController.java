package com.maiservices.maiservices.controller;

import com.maiservices.maiservices.dto.ApiResponse;
import com.maiservices.maiservices.dto.auth.AuthResponse;
import com.maiservices.maiservices.dto.auth.LoginRequest;
import com.maiservices.maiservices.dto.auth.RefreshTokenRequest;
import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.entity.Token;
import com.maiservices.maiservices.entity.User;
import com.maiservices.maiservices.dto.auth.RegisterRequest;
import com.maiservices.maiservices.dto.auth.TokenValidationResponse;
import com.maiservices.maiservices.repository.TokenRepository;
import com.maiservices.maiservices.repository.UserRepository;
import com.maiservices.maiservices.security.JwtService;
import com.maiservices.maiservices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.maiservices.maiservices.entity.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.HashSet;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

        @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (body != null && body.containsKey("token")) {
            token = body.get("token");
        }
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("Token is missing", null)
            );
        }
        Optional<Token> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired() || tokenOpt.get().isRevoked()) {
            return ResponseEntity.ok(ApiResponse.success("Token is invalid or expired", TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token is invalid or expired")
                    .build()));
        }
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Token is malformed", TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token is malformed")
                    .build()));
        }
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("User not found", TokenValidationResponse.builder()
                    .valid(false)
                    .message("User not found")
                    .build()));
        }
        User user = userOpt.get();
        boolean valid = jwtService.isTokenValid(token, user);
        if (!valid) {
            return ResponseEntity.ok(ApiResponse.success("Token is invalid", TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token is invalid")
                    .build()));
        }
        // Collect roles and permissions
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            roles.add(role.getName());
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getName());
            }
        }
        TokenValidationResponse response = TokenValidationResponse.builder()
                .valid(true)
                .username(user.getUsername())
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .permissions(permissions)
                .message("Token is valid")
                .build();
        return ResponseEntity.ok(ApiResponse.success("Token is valid", response));
    }

}
