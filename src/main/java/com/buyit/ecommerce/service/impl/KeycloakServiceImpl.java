package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.exception.custom.KeycloakIntegrationException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.service.KeycloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Getter
    @Value("${keycloak.realm.name}")
    private String realmName;

    @Value("${keycloak.backend.client-id}")
    private String clientId;

    @Getter
    @Value("${keycloak.server-url}")
    private String authServerUrl;

    @Value("${keycloak.email-verified}")
    private boolean emailVerified;


    @Override
    public void getUserSessions(String userId) {
        List<UserSessionRepresentation> userSessions = getUsersResource()
                .get(userId)
                .getUserSessions();

        ObjectMapper mapper = new ObjectMapper();

        userSessions.forEach(session -> {
            try {
                log.info(
                        "{}",
                        mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(session)
                );
            } catch (Exception e) {
                log.error("Error serializando sesión", e);
            }
        });

    }

    @Override
    public List<RoleRepresentation> getClientRoles() {

        ClientRepresentation backendClient =
                getClientRepresentation();

        return getRealmResource()
                .clients()
                .get(backendClient.getId())
                .roles()
                .list();
    }

    @Override
    public String createUserInKeycloak(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = buildKeycloakUserRepresentation(userRegisterDTO);
        try (Response response = getUsersResource().create(keycloakUser)) {
            if (response.getStatus() != 201) {
                Map<String, String> respuesta = response.readEntity(Map.class);
                log.info("Respuesta: {}", respuesta);
                throw new KeycloakIntegrationException(respuesta.get("errorMessage"));
            }
            return extractUserIdFromResponse(response);
        }
    }

    private String extractUserIdFromResponse(Response response) {
        String path = response.getLocation().getPath();
        int lastSlash = path.lastIndexOf('/');
        return (lastSlash != -1) ? path.substring(lastSlash + 1) : path;
    }

    @Override
    public void assignDefaultRoleToUser(String keycloakUserId, String userRole) {

        RoleRepresentation roleToAssign = getRoleFromKeycloak(userRole);
        UserResource userResource = getUsersResource().get(keycloakUserId);
        userResource
                .roles()
                .clientLevel(getClientRepresentation().getId())
                .add(List.of(roleToAssign));
    }

    @Async("taskExecutor")
    @Override
    public void sendKeycloakVerifyEmail(String keycloakId) {
        try {
            if (!emailVerified) {
                getUsersResource().get(keycloakId).sendVerifyEmail();
            }
        } catch (InternalServerErrorException e) {
            log.error("Keycloak Error: {}", e.getMessage());
            throw new KeycloakIntegrationException(e.getMessage());
        }
    }



    @Override
    public boolean hasOTP(String userId) {
        List<CredentialRepresentation> credentials = getRealmResource()
                .users()
                .get(userId)
                .credentials();

        return credentials.stream().anyMatch(c -> "otp".equals(c.getType()));
    }

    @Override
    public void startOTPSetup(String userId) {
        UserResource userResource = getUsersResource().get(userId);
        UserRepresentation userRep = userResource.toRepresentation();

        List<String> actions =
                Optional.ofNullable(userRep.getRequiredActions())
                        .orElse(new ArrayList<>());

        if (!actions.contains("CONFIGURE_TOTP")) {
            actions.add("CONFIGURE_TOTP");
        }

        userRep.setRequiredActions(actions);
        userResource.update(userRep);
    }

    @Override
    public void disableOTP(String userId) {
        UserResource user = getUsersResource().get(userId);
        List<CredentialRepresentation> credentials = user.credentials();
        credentials.stream().filter(c -> "otp".equals(c.getType()))
                .forEach(c -> user.removeCredential(c.getId()));
    }

    @Override
    public void deleteUserFromKeycloak(String username) {

        String userId = "";
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> users = usersResource.search(username, true);
        try {
            if (!users.isEmpty()) {
                userId = users.get(0).getId();
                usersResource.delete(userId);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Error al eliminar usuario en Keycloak. ID: %s, Error: %s",
                    userId, e.getMessage());
            log.error(errorMessage);
            throw new KeycloakIntegrationException(errorMessage);
        }
    }


    private RoleRepresentation getRoleFromKeycloak(String roleName) {

        ClientRepresentation backendClient =
                getClientRepresentation();

        return getRealmResource()
                .clients()
                .get(backendClient.getId())
                .roles()
                .get(roleName)
                .toRepresentation();
    }

    private UserRepresentation buildKeycloakUserRepresentation(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userRegisterDTO.getUserName());
        keycloakUser.setEmail(userRegisterDTO.getEmail());
        keycloakUser.setFirstName(userRegisterDTO.getFirstName());
        keycloakUser.setLastName(userRegisterDTO.getLastName());
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(emailVerified);

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

        Optional<ClientRepresentation> client =
                getRealmResource()
                        .clients()
                        .findByClientId(clientId)
                        .stream()
                        .findFirst();

        return client.orElseThrow(
                () -> new ResourceNotFoundException(
                        "Client not found: " + clientId
                )
        );
    }
}
