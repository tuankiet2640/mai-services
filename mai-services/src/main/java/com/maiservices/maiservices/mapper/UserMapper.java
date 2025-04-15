package com.maiservices.maiservices.mapper;

import com.maiservices.maiservices.dto.RoleDto;
import com.maiservices.maiservices.dto.UserDto;
import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.entity.User;

/**
 * Mapper interface for converting between User entities and DTOs
 */
public interface UserMapper {

    /**
     * Convert a User entity to a UserDto
     *
     * @param user the user entity to convert
     * @return the user DTO
     */
    UserDto toDto(User user);

    /**
     * Convert a Role entity to a RoleDto
     *
     * @param role the role entity to convert
     * @return the role DTO
     */
    RoleDto toRoleDto(Role role);

    /**
     * Convert a UserDto to a User entity
     *
     * @param userDto the user DTO to convert
     * @return the user entity
     */
    User toEntity(UserDto userDto);
}
