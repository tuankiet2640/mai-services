package com.maiservices.maiservices.service;

import com.maiservices.maiservices.dto.PermissionDto;
import com.maiservices.maiservices.dto.RoleDto;
import com.maiservices.maiservices.dto.UserDto;
import com.maiservices.maiservices.entity.Permission;
import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .roles(mapRoles(user.getRoles()))
                .build();
    }

    private Set<RoleDto> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }

        return roles.stream()
                .map(this::toRoleDto)
                .collect(Collectors.toSet());
    }

    public RoleDto toRoleDto(Role role) {
        if (role == null) {
            return null;
        }

        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(mapPermissions(role.getPermissions()))
                .build();
    }

    private Set<PermissionDto> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }

        return permissions.stream()
                .map(this::toPermissionDto)
                .collect(Collectors.toSet());
    }

    private PermissionDto toPermissionDto(Permission permission) {
        if (permission == null) {
            return null;
        }

        return PermissionDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.isEnabled());
        user.setPassword(userDto.getPassword());
        
        return user;
    }
}
