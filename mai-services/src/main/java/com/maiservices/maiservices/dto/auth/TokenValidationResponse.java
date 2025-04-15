package com.maiservices.maiservices.dto.auth;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private String username;
    private String message;
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private Set<String> permissions;
}
