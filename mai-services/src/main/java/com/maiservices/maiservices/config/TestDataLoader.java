package com.maiservices.maiservices.config;

import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.entity.User;
import com.maiservices.maiservices.repository.RoleRepository;
import com.maiservices.maiservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Loads test data only in development profile
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class TestDataLoader {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadTestData() {
        return args -> {
            // Only load test data if we don't have any test users yet
            if (userRepository.findByUsername("testuser").isPresent()) {
                log.info("Test data already exists, skipping test data creation");
                return;
            }

            log.info("Loading test data...");

            // Get the USER role
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));

            // Create a test user
            User testUser = User.builder()
                    .username("testuser")
                    .email("test@maiservices.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Test")
                    .lastName("User")
                    .enabled(true)
                    .roles(Set.of(userRole))
                    .build();

            userRepository.save(testUser);
            log.info("Created test user with username: testuser and password: password123");
        };
    }
}
