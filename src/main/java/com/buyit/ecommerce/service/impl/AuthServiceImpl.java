package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.exception.custom.KeycloakIntegrationException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.service.RoleService;
import com.buyit.ecommerce.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final Client httpClient;
    private final KeycloakService keycloakService;


    @Override
    @Transactional
    public void createUser(UserRegisterDTO userRegisterDTO) {

        Role role = roleService.findByName(userRegisterDTO.getRole());

        boolean userExists = userService.userExistsInDatabase(userRegisterDTO.getEmail(), userRegisterDTO.getUserName());

        if (userExists) {
            throw new ResourceExistException("Email or username is already in use");
        }

        String keycloakUserId = keycloakService.createUserInKeycloak(userRegisterDTO);
        if (keycloakUserId == null) {
            throw new KeycloakIntegrationException("Failed to create user in Keycloak.");
        }

        keycloakService.assignDefaultRoleToUser(keycloakUserId, role.getName());
        userService.saveUserToDatabase(userRegisterDTO, keycloakUserId);

        keycloakService.sendKeycloakVerifyEmail(keycloakUserId);

        log.info("User '{}' created successfully.", userRegisterDTO.getUserName());
    }

    @Override
    public AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException {

        Response response = httpClient.target(keycloakService.getServerToken())
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(new jakarta.ws.rs.core.Form()
                        .param("grant_type", "password")
                        .param("client_id", keycloakService.getClientId())
                        .param("client_secret", keycloakService.getClientSecret())
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
}
