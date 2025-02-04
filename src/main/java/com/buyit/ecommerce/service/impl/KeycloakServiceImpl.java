package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Getter
    @Value("${keycloak.realm.name}")
    private String realmName;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Getter
    @Value("${keycloak.server-url}")
    private String authServerUrl;


    @Override
    public List<RoleRepresentation> getClientRoles() {
        ClientRepresentation client = getClientRepresentation();
        return getRealmResource().clients().get(client.getId()).roles().list();

    }

    @Override
    public String getClientInternalId() {
        ClientRepresentation client = getClientRepresentation();
        return client.getId();
    }

    @Override
    public String getClientId() {
        ClientRepresentation client = getClientRepresentation();
        return client.getClientId();
    }

    @Override
    public String getClientSecret() {
        ClientRepresentation client = getClientRepresentation();
        return client.getSecret();
    }

    @Override
    public String createUserInKeycloak(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = buildKeycloakUserRepresentation(userRegisterDTO);
        try (Response response = getUsersResource().create(keycloakUser)) {
            if (response.getStatus() != 201) {
                log.error("Keycloak Error: {}", response.readEntity(String.class));
                return null;
            }
            return extractUserIdFromResponse(response);
        }
    }

    private String extractUserIdFromResponse(Response response) {
        return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
    }

    @Override
    public void assignDefaultRoleToUser(String keycloakUserId, String userRole) {


        String clientInternalId = getClientInternalId();
        RoleRepresentation roleToAssign = getRoleFromKeycloak(userRole);

        UserResource userResource = getUsersResource().get(keycloakUserId);
        userResource.roles().clientLevel(clientInternalId).add(List.of(roleToAssign));

        log.info("Assigned role '{}' to user with ID '{}'.", roleToAssign.getName(), keycloakUserId);
    }

    @Async("taskExecutor")
    @Override
    public void sendKeycloakVerifyEmail(String keycloakId) {
        getUsersResource().get(keycloakId).sendVerifyEmail();
    }

    @Override
    public String getServerUrl() {
        return getAuthServerUrl();
    }

    @Override
    public String getAuthUrl(){
        return getAuthServerUrl() + "/realms/" + getRealmName() + "/protocol/openid-connect/auth";
    }

    @Override
    public String getServerToken() {
        return getAuthServerUrl() + "/realms/" + getRealmName() + "/protocol/openid-connect/token";
    }


    private RoleRepresentation getRoleFromKeycloak(String roleName) {
        return getClientRoles().stream()
                .filter(role -> role.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found"));
    }

    private UserRepresentation buildKeycloakUserRepresentation(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userRegisterDTO.getUserName());
        keycloakUser.setEmail(userRegisterDTO.getEmail());
        keycloakUser.setFirstName(userRegisterDTO.getFirstName());
        keycloakUser.setLastName(userRegisterDTO.getLastName());
        keycloakUser.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRegisterDTO.getPassword());
        credential.setTemporary(false);
        keycloakUser.setCredentials(List.of(credential));
        return keycloakUser;
    }


    public RealmResource getRealmResource() {
        return keycloak.realm(realmName);
    }

    public UsersResource getUsersResource() {
        return getRealmResource().users();
    }

    public ClientRepresentation getClientRepresentation() {
        return getRealmResource()
                .clients()
                .findByClientId(clientId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Client with clientId '" + clientId + "' not found"));
    }

}
