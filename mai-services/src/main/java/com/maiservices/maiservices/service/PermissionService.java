package com.maiservices.maiservices.service;

import com.maiservices.maiservices.dto.PermissionDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for permission management operations
 */
public interface PermissionService {

    /**
     * Get all permissions
     *
     * @return list of all permission DTOs
     */
    List<PermissionDto> getAllPermissions();

    /**
     * Get a permission by ID
     *
     * @param id the permission ID
     * @return the permission DTO
     */
    PermissionDto getPermissionById(UUID id);

    /**
     * Get a permission by name
     *
     * @param name the permission name
     * @return the permission DTO
     */
    PermissionDto getPermissionByName(String name);

    /**
     * Create a new permission
     *
     * @param permissionDto the permission data
     * @return the created permission DTO
     */
    PermissionDto createPermission(PermissionDto permissionDto);

    /**
     * Update an existing permission
     *
     * @param id the permission ID to update
     * @param permissionDto the updated permission data
     * @return the updated permission DTO
     */
    PermissionDto updatePermission(UUID id, PermissionDto permissionDto);

    /**
     * Delete a permission by ID
     *
     * @param id the permission ID to delete
     */
    void deletePermission(UUID id);
}
