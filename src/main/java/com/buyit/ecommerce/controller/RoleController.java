package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.endpoint.CreateEndpointRequest;
import com.buyit.ecommerce.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @PostMapping("/sync")
    public void syncKeycloakRoles() {
        roleService.syncKeycloakRoles();
    }

    @PostMapping("/{id}/endpoints")
    public void addEndpointToRole(@PathVariable("id") Long id, @RequestBody CreateEndpointRequest endpointsIds) {
        roleService.assignRolesToEndpoint(id,endpointsIds);
    }
}
