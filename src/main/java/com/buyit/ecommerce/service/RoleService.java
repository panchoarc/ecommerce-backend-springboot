package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.endpoint.CreateEndpointRequest;
import com.buyit.ecommerce.entity.Role;

public interface RoleService {


    void syncKeycloakRoles();

    void assignRolesToEndpoint(Long id, CreateEndpointRequest endpointsIds);

    Role findByName(String name);
}
