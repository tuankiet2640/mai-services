package com.maiservices.maiservices.service;

import com.maiservices.maiservices.dto.RoleDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for role management operations
 */
public interface RoleService {

    /**
     * Get all roles
     *
     * @return list of all role DTOs
     */
    List<RoleDto> getAllRoles();

    /**
     * Get a role by ID
     *
     * @param id the role ID
     * @return the role DTO
     */
    RoleDto getRoleById(UUID id);

    /**
     * Get a role by name
     *
     * @param name the role name
     * @return the role DTO
     */
    RoleDto getRoleByName(String name);

    /**
     * Create a new role
     *
     * @param roleDto the role data
     * @return the created role DTO
     */
    RoleDto createRole(RoleDto roleDto);

    /**
     * Update an existing role
     *
     * @param id the role ID to update
     * @param roleDto the updated role data
     * @return the updated role DTO
     */
    RoleDto updateRole(UUID id, RoleDto roleDto);

    /**
     * Delete a role by ID
     *
     * @param id the role ID to delete
     */
    void deleteRole(UUID id);

    /**
     * Assign a permission to a role
     *
     * @param roleId the role ID
     * @param permissionId the permission ID to assign
     * @return the updated role DTO
     */
    RoleDto assignPermissionToRole(UUID roleId, UUID permissionId);

    /**
     * Remove a permission from a role
     *
     * @param roleId the role ID
     * @param permissionId the permission ID to remove
     * @return the updated role DTO
     */
    RoleDto removePermissionFromRole(UUID roleId, UUID permissionId);

    /**
     * Update all permissions for a role
     *
     * @param roleId the role ID
     * @param permissionIds the set of permission IDs to assign to the role
     * @return the updated role DTO
     */
    RoleDto updateRolePermissions(UUID roleId, Set<UUID> permissionIds);
}
