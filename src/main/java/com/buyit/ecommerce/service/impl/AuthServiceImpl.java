package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.KeycloakIntegrationException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.RoleRepository;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.EmailService;
import com.buyit.ecommerce.util.KeycloakProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final KeycloakProvider keycloakProvider;
    private final UsersRepository usersRepository;
    private final EmailService emailService;
    private final Client httpClient;
    private final RoleRepository roleRepository;


    @Override
    @Transactional
    public void createUser(UserRegisterDTO userRegisterDTO) {

        Optional<Role> role = roleRepository.findByName(userRegisterDTO.getRole());
        if (role.isEmpty()) {
            throw new ResourceExistException("Role not valid");
        }

        if (userExistsInDatabase(userRegisterDTO.getEmail(), userRegisterDTO.getUserName())) {
            throw new ResourceExistException("Email or username is already in use");
        }

        String keycloakUserId = createUserInKeycloak(userRegisterDTO);
        if (keycloakUserId == null) {
            throw new KeycloakIntegrationException("Failed to create user in Keycloak.");
        }

        assignDefaultRoleToUser(keycloakUserId, role.get().getName());
        saveUserToDatabase(userRegisterDTO, keycloakUserId);

        emailService.sendKeycloakVerifyEmail(keycloakUserId);

        log.info("User '{}' created successfully.", userRegisterDTO.getEmail());
    }

    @Override
    public AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException {

        Response response = httpClient.target(getAuthUrl())
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(new jakarta.ws.rs.core.Form()
                        .param("grant_type", "password")
                        .param("client_id", keycloakProvider.getClientId())
                        .param("client_secret", keycloakProvider.getClientSecret())
                        .param("username", userLoginDTO.getUserName())
                        .param("password", userLoginDTO.getPassword())));

        String responseBody = response.readEntity(String.class);
        if (response.getStatus() != 200) {
            log.error("Keycloak login failed: {}", responseBody);
            throw new ResourceNotFoundException("Invalid username or password. Details: " + responseBody);
        }

        AccessTokenResponse tokenResponse = new ObjectMapper().readValue(responseBody, AccessTokenResponse.class);
        log.info("User '{}' logged in successfully.", userLoginDTO.getUserName());

        log.info("Respuesta del token: {}", tokenResponse.getTokenType());
        return tokenResponse;
    }

    private boolean userExistsInDatabase(String email, String username) {

        return usersRepository.findByEmailOrUserName(email, username).isPresent();
    }

    private String createUserInKeycloak(UserRegisterDTO userRegisterDTO) {
        UserRepresentation keycloakUser = buildKeycloakUserRepresentation(userRegisterDTO);
        try (Response response = keycloakProvider.usersResource().create(keycloakUser)) {
            if (response.getStatus() != 201) {
                log.error("Keycloak Error: {}", response.readEntity(String.class));
                return null;
            }
            return extractUserIdFromResponse(response);
        }
    }

    private void assignDefaultRoleToUser(String keycloakUserId, String userRole) {


        String clientInternalId = keycloakProvider.getClientInternalId();
        RoleRepresentation roleToAssign = getRoleFromKeycloak(userRole);

        UserResource userResource = keycloakProvider.usersResource().get(keycloakUserId);
        userResource.roles().clientLevel(clientInternalId).add(List.of(roleToAssign));

        log.info("Assigned role '{}' to user with ID '{}'.", roleToAssign.getName(), keycloakUserId);
    }

    private void saveUserToDatabase(UserRegisterDTO userRegisterDTO, String keycloakUserId) {
        User newUser = new User();
        newUser.setEmail(userRegisterDTO.getEmail());
        newUser.setFirstName(userRegisterDTO.getFirstName());
        newUser.setUserName(userRegisterDTO.getUserName());
        newUser.setLastName(userRegisterDTO.getLastName());
        newUser.setKeycloakUserId(keycloakUserId);
        usersRepository.save(newUser);
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

    private String extractUserIdFromResponse(Response response) {
        return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
    }

    private RoleRepresentation getRoleFromKeycloak(String roleName) {
        return keycloakProvider.getClientRoles().stream()
                .filter(role -> role.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role '" + roleName + "' not found"));
    }

    private String getAuthUrl() {
        return keycloakProvider.getAuthServerUrl() + "/realms/" + keycloakProvider.getRealmName() + "/protocol/openid-connect/token";
    }
}
