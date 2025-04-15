package com.maiservices.maiservices.service.impl;

import com.maiservices.maiservices.dto.PermissionDto;
import com.maiservices.maiservices.entity.Permission;
import com.maiservices.maiservices.repository.PermissionRepository;
import com.maiservices.maiservices.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDto getPermissionById(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        return mapToDto(permission);
    }

    @Override
    public PermissionDto getPermissionByName(String name) {
        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permission not found with name: " + name));
        return mapToDto(permission);
    }

    @Override
    @Transactional
    public PermissionDto createPermission(PermissionDto permissionDto) {
        // Check if permission name already exists
        if (permissionRepository.existsByName(permissionDto.getName())) {
            throw new RuntimeException("Permission name already exists: " + permissionDto.getName());
        }

        Permission permission = new Permission();
        permission.setName(permissionDto.getName());
        permission.setDescription(permissionDto.getDescription());

        Permission savedPermission = permissionRepository.save(permission);
        return mapToDto(savedPermission);
    }

    @Override
    @Transactional
    public PermissionDto updatePermission(UUID id, PermissionDto permissionDto) {
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));

        // Check if name is being changed and if it already exists
        if (!existingPermission.getName().equals(permissionDto.getName()) && 
                permissionRepository.existsByName(permissionDto.getName())) {
            throw new RuntimeException("Permission name already exists: " + permissionDto.getName());
        }

        existingPermission.setName(permissionDto.getName());
        existingPermission.setDescription(permissionDto.getDescription());

        Permission updatedPermission = permissionRepository.save(existingPermission);
        return mapToDto(updatedPermission);
    }

    @Override
    @Transactional
    public void deletePermission(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));

        // Check if permission is assigned to any roles
        if (!permission.getRoles().isEmpty()) {
            throw new RuntimeException("Cannot delete permission as it is assigned to roles");
        }

        permissionRepository.deleteById(id);
    }

    private PermissionDto mapToDto(Permission permission) {
        return PermissionDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
