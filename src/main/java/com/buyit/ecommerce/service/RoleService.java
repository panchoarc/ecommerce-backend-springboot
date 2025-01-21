package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.endpoint.CreateEndpointRequest;

public interface RoleService {


    void syncKeycloakRoles();

    void assignRolesToEndpoint(Long id, CreateEndpointRequest endpointsIds);
}
