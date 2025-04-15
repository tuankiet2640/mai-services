package com.maiservices.maiservices.service;

import com.maiservices.maiservices.dto.auth.AuthResponse;
import com.maiservices.maiservices.dto.auth.LoginRequest;
import com.maiservices.maiservices.dto.auth.RefreshTokenRequest;
import com.maiservices.maiservices.dto.auth.RegisterRequest;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    /**
     * Register a new user
     *
     * @param request the registration request containing user details
     * @return authentication response with tokens and user information
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate a user and generate tokens
     *
     * @param request the login request containing credentials
     * @return authentication response with tokens and user information
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh an access token using a valid refresh token
     *
     * @param request the refresh token request
     * @return authentication response with new access token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);
}
