package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.RequirePermission;
import com.buyit.ecommerce.constants.PermissionsConstants;
import com.buyit.ecommerce.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @RequirePermission(value = PermissionsConstants.ROLES_SYNCHRONIZE_ROLES)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ROLES_SYNCHRONIZE_ROLES + "')")
    @PostMapping("/sync")
    public void syncKeycloakRoles() {
        roleService.syncKeycloakRoles();
    }


    @RequirePermission(value = PermissionsConstants.ROLES_ASSIGN_PERMISSIONS)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.ROLES_ASSIGN_PERMISSIONS + "')")
    @PutMapping("/{id}/permissions")
    public void assignPermissionsToRole(@PathVariable("id") Long roleId, @RequestBody List<Long> permissionsId) {

        roleService.assignPermissionsToRole(roleId, permissionsId);
    }

}
