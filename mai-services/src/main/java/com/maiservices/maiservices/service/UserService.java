package com.maiservices.maiservices.service;

import com.maiservices.maiservices.dto.UserDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for user management operations
 */
public interface UserService {

    /**
     * Get all users
     *
     * @return list of all user DTOs
     */
    List<UserDto> getAllUsers();

    /**
     * Get a user by ID
     *
     * @param id the user ID
     * @return the user DTO
     */
    UserDto getUserById(UUID id);

    /**
     * Get a user by username
     *
     * @param username the username
     * @return the user DTO
     */
    UserDto getUserByUsername(String username);

    /**
     * Update an existing user
     *
     * @param id the user ID to update
     * @param userDto the updated user data
     * @return the updated user DTO
     */
    UserDto updateUser(UUID id, UserDto userDto);

    /**
     * Delete a user by ID
     *
     * @param id the user ID to delete
     */
    void deleteUser(UUID id);

    /**
     * Assign a role to a user
     *
     * @param userId the user ID
     * @param roleId the role ID to assign
     * @return the updated user DTO
     */
    UserDto assignRoleToUser(UUID userId, UUID roleId);

    /**
     * Remove a role from a user
     *
     * @param userId the user ID
     * @param roleId the role ID to remove
     * @return the updated user DTO
     */
    UserDto removeRoleFromUser(UUID userId, UUID roleId);

    /**
     * Update all roles for a user
     *
     * @param userId the user ID
     * @param roleIds the set of role IDs to assign to the user
     * @return the updated user DTO
     */
    UserDto updateUserRoles(UUID userId, Set<UUID> roleIds);
}
