package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.entity.Permission;
import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.entity.RolePermission;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.PermissionRepository;
import com.buyit.ecommerce.repository.RolePermissionRepository;
import com.buyit.ecommerce.repository.RoleRepository;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final KeycloakService keycloakService;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public void syncKeycloakRoles() {

        List<RoleRepresentation> roles = keycloakService.getClientRoles();
        for (RoleRepresentation role : roles) {
            String roleId = role.getId(); // ID único del rol
            String roleName = role.getName(); // Nombre del rol (puede cambiar)

            // Sincronizar con la base de datos
            syncRoleWithDatabase(roleId, roleName);
        }

    }

    @Override
    public Role findByName(String name) {
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isEmpty()) {
            throw new ResourceExistException("Role not valid");
        }
        return role.get();
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionsId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<RolePermission> currentRelations = role.getRolePermissions();


        Set<Long> currentIds = rolePermissionRepository.findPermissionIdsByRoleId(roleId);

        Set<Long> newIds = new HashSet<>(permissionsId);

        // 🧹 1. ELIMINAR solo los que sobran
        currentRelations.removeIf(rp -> !newIds.contains(rp.getPermission().getId()));

        // ➕ 2. CALCULAR solo los que faltan (evita query innecesaria)
        Set<Long> idsToAdd = new HashSet<>(newIds);
        idsToAdd.removeAll(currentIds);

        if (!idsToAdd.isEmpty()) {

            List<Permission> permissionsToAdd = permissionRepository.findAllById(idsToAdd);

            for (Permission permission : permissionsToAdd) {
                RolePermission rp = new RolePermission();
                rp.setRole(role);
                rp.setPermission(permission);
                rp.setGrantedAt(LocalDateTime.now());
                rp.setGrantedBy("system");
                rp.setIsActive(true);

                currentRelations.add(rp);
            }
        }
    }

    @Override
    @Transactional
    public void assignAdminPermissions() {

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        List<Permission> missingPermissions =
                permissionRepository.findMissingPermissions(adminRole.getId());

        LocalDateTime now = LocalDateTime.now();

        missingPermissions.forEach(permission -> {
            RolePermission rp = new RolePermission();
            rp.setRole(adminRole);
            rp.setPermission(permission);
            rp.setGrantedAt(now);
            rp.setGrantedBy("system");
            rp.setIsActive(true);

            adminRole.getRolePermissions().add(rp);
        });
    }

    private void syncRoleWithDatabase(String roleId, String roleName) {
        // Busca si el rol ya existe en la base de datos por su ID
        Role existingRole = roleRepository.findByExternalId(roleId);

        if (existingRole == null) {
            // Si no existe, crear un nuevo registro
            Role newRole = new Role();
            newRole.setExternalId(roleId);
            newRole.setName(roleName);
            newRole.setIsActive(true);
            roleRepository.save(newRole);
        } else {
            // Si existe, actualiza el nombre si cambió
            if (!existingRole.getName().equals(roleName)) {
                existingRole.setName(roleName);
                roleRepository.save(existingRole);
            }
        }
    }
}
