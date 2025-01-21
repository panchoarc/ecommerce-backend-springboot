package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.repository.RoleEndpointRepository;
import com.buyit.ecommerce.service.PermissionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionsServiceImpl implements PermissionsService {

    private final EndpointRepository endpointRepository;
    private final RoleEndpointRepository roleEndpointRepository;

    @Override
    public boolean hasAccess(String endpointUrl, String method, Collection<String> userRoles) {

        Optional<Endpoint> endpoint = endpointRepository.findByUrlAndHttpMethod(endpointUrl, method);
        if (endpoint.isEmpty()) {
            throw new ResourceNotFoundException("El endpoint no existe: " + endpointUrl);
        }
        if (Boolean.TRUE.equals(endpoint.get().getIsPublic())) {
            return true;
        }
        List<String> rolesWithAccess = roleEndpointRepository.findRoleIdsByEndpoint(endpoint.get().getId());


        // Comparar roles del usuario con los roles que tienen acceso
        for (String role : userRoles) {
            if (rolesWithAccess.contains(role)) {
                return true;
            }
        }

        return false; // No tiene acceso
    }
}
