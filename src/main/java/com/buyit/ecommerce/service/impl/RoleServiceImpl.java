package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.endpoint.CreateEndpointRequest;
import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.entity.RoleEndpoint;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.repository.RoleEndpointRepository;
import com.buyit.ecommerce.repository.RoleRepository;
import com.buyit.ecommerce.service.RoleService;
import com.buyit.ecommerce.util.KeycloakProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final KeycloakProvider keycloakProvider;
    private final RoleRepository roleRepository;
    private final EndpointRepository endpointRepository;
    private final RoleEndpointRepository roleEndpointRepository;

    @Override
    public void syncKeycloakRoles() {

        List<RoleRepresentation> roles = keycloakProvider.getClientRoles();
        for (RoleRepresentation role : roles) {
            String roleId = role.getId(); // ID único del rol
            String roleName = role.getName(); // Nombre del rol (puede cambiar)

            // Sincronizar con la base de datos
            syncRoleWithDatabase(roleId, roleName);
        }

    }

    @Override
    public void assignRolesToEndpoint(Long id, CreateEndpointRequest endpointsIds) {
        Role role = findRoleById(id);
        List<Endpoint> endpoints = endpointRepository.findAllById(endpointsIds.getEndpointIds());

        boolean isValidEndpoints = isEndpointsValid(endpointsIds.getEndpointIds());

        if (!isValidEndpoints) {
            throw new ResourceNotFoundException("Some endpoints IDs are not valid");
        }
        updateRoleEndpoints(endpoints, role);
    }


    public void updateRoleEndpoints(List<Endpoint> endpoints, Role role) {

        // Obtener los endpoints existentes para el rol
        List<RoleEndpoint> existingRoleEndpoints = roleEndpointRepository.findByRole(role);

        // Crear un conjunto con los IDs de los endpoints existentes para búsquedas más rápidas
        Set<Long> existingEndpointIds = existingRoleEndpoints.stream()
                .map(roleEndpoint -> roleEndpoint.getEndpoint().getId())
                .filter(Objects::nonNull) // Asegurarnos de que no haya null
                .collect(Collectors.toSet());

        // Filtrar los nuevos endpoints que no están asociados al rol
        List<RoleEndpoint> newRoleEndpoints = endpoints.stream()
                .filter(endpoint -> !existingEndpointIds.contains(endpoint.getId()))
                .map(endpoint -> {
                    // Crear la nueva relación RoleEndpoint
                    RoleEndpoint roleEndpoint = new RoleEndpoint();
                    roleEndpoint.setRole(role);
                    roleEndpoint.setEndpoint(endpoint);
                    roleEndpoint.setActive(true);
                    return roleEndpoint;
                })
                .toList();

        // Guardar todas las nuevas relaciones en una sola operación
        if (!newRoleEndpoints.isEmpty()) {
            roleEndpointRepository.saveAll(newRoleEndpoints);
        }
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


    private Role findRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    public boolean isEndpointsValid(List<Long> endpointsIds) {
        if (endpointsIds == null || endpointsIds.isEmpty()) {
            return false;  // Si la lista está vacía o es nula, devuelve false
        }

        log.info("Checking endpoints validity for {}", endpointsIds.size());

        // Obtener todas las categorías correspondientes a los categoryIds de una sola vez
        List<Endpoint> endpoints = endpointRepository.findAllById(endpointsIds);

        // Verificar si todas las categorías existen y están activas
        return endpointsIds.size() == endpoints.size() &&
                endpoints.stream().allMatch(Endpoint::getIsActive);
    }
}
