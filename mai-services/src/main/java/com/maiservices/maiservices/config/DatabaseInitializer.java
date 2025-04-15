package com.maiservices.maiservices.config;

import com.maiservices.maiservices.entity.Permission;
import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.entity.User;
import com.maiservices.maiservices.repository.PermissionRepository;
import com.maiservices.maiservices.repository.RoleRepository;
import com.maiservices.maiservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Database already initialized, skipping initialization");
            return;
        }

        log.info("Initializing database with default roles and permissions");
        
        // Create permissions
        List<Permission> permissions = createPermissions();
        
        // Create roles and assign permissions
        List<Role> roles = createRoles(permissions);
        
        // Create admin user
        createAdminUser(roles);
        
        log.info("Database initialization completed successfully");
    }

    private List<Permission> createPermissions() {
        List<String> permissionNames = Arrays.asList(
                "user:read", "user:write", "user:delete",
                "role:read", "role:write", "role:delete",
                "permission:read", "permission:write", "permission:delete"
        );

        List<Permission> permissions = permissionNames.stream()
                .map(name -> {
                    Permission permission = Permission.builder()
                            .id(UUID.randomUUID())
                            .name(name)
                            .description("Permission to " + name.replace(":", " "))
                            .build();
                    return permissionRepository.save(permission);
                })
                .toList();

        log.info("Created {} permissions", permissions.size());
        return permissions;
    }

    private List<Role> createRoles(List<Permission> allPermissions) {
        // Admin role with all permissions
        Role adminRole = Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN")
                .description("Administrator role with full access")
                .permissions(new HashSet<>(allPermissions))
                .build();
        adminRole = roleRepository.save(adminRole);

        // User role with limited permissions
        Set<Permission> userPermissions = allPermissions.stream()
                .filter(p -> p.getName().startsWith("user:read"))
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        Role userRole = Role.builder()
                .id(UUID.randomUUID())
                .name("USER")
                .description("Regular user with limited access")
                .permissions(userPermissions)
                .build();
        userRole = roleRepository.save(userRole);

        log.info("Created ADMIN and USER roles");
        return Arrays.asList(adminRole, userRole);
    }

    private void createAdminUser(List<Role> roles) {
        Role adminRole = roles.stream()
                .filter(role -> role.getName().equals("ADMIN"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        if (!userRepository.existsByUsername("admin")) {
            User adminUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("admin")
                    .email("admin@maiservices.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
            
            userRepository.save(adminUser);
            log.info("Created admin user with username: admin and password: admin123");
        }
    }
}
