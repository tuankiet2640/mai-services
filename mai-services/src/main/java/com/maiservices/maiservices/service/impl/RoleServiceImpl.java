package com.maiservices.maiservices.service.impl;

import com.maiservices.maiservices.dto.RoleDto;
import com.maiservices.maiservices.entity.Permission;
import com.maiservices.maiservices.entity.Role;
import com.maiservices.maiservices.mapper.UserMapper;
import com.maiservices.maiservices.repository.PermissionRepository;
import com.maiservices.maiservices.repository.RoleRepository;
import com.maiservices.maiservices.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserMapper userMapper;

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDto getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return mapToDto(role);
    }

    @Override
    public RoleDto getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
        return mapToDto(role);
    }

    @Override
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        // Check if role name already exists
        if (roleRepository.existsByName(roleDto.getName())) {
            throw new RuntimeException("Role name already exists: " + roleDto.getName());
        }

        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());

        Role savedRole = roleRepository.save(role);
        return mapToDto(savedRole);
    }

    @Override
    @Transactional
    public RoleDto updateRole(UUID id, RoleDto roleDto) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check if name is being changed and if it already exists
        if (!existingRole.getName().equals(roleDto.getName()) && 
                roleRepository.existsByName(roleDto.getName())) {
            throw new RuntimeException("Role name already exists: " + roleDto.getName());
        }

        existingRole.setName(roleDto.getName());
        existingRole.setDescription(roleDto.getDescription());

        Role updatedRole = roleRepository.save(existingRole);
        return mapToDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        // Check if role is assigned to any users
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role as it is assigned to users");
        }

        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RoleDto assignPermissionToRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        Role updatedRole = roleRepository.save(role);
        return mapToDto(updatedRole);
    }

    @Override
    @Transactional
    public RoleDto removePermissionFromRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        Role updatedRole = roleRepository.save(role);
        return mapToDto(updatedRole);
    }

    @Override
    @Transactional
    public RoleDto updateRolePermissions(UUID roleId, Set<UUID> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Set<Permission> permissions = permissionIds.stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId)))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);
        return mapToDto(updatedRole);
    }

    private RoleDto mapToDto(Role role) {
        return userMapper.toRoleDto(role);
    }
}
