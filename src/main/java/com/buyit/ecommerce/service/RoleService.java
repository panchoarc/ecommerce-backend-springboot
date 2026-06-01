package com.buyit.ecommerce.service;

import com.buyit.ecommerce.entity.Role;

import java.util.List;

public interface RoleService {

    void syncKeycloakRoles();
    Role findByName(String name);

    void assignPermissionsToRole(Long roleId, List<Long> permissionsId);

    void assignAdminPermissions();
}
