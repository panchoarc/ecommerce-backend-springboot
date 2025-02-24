package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.AuthenticationException;
import com.buyit.ecommerce.exception.custom.KeycloakIntegrationException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.UsersRepository;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.service.RoleService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.ValidationHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final KeycloakService keycloakService;
    private final RestTemplate restTemplate;
    private final ValidationHelper validationHelper;
    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void createUser(UserRegisterDTO userRegisterDTO) {

        validationHelper.validate(userRegisterDTO);

        String queriedRole = userRegisterDTO.getRole() != null ? userRegisterDTO.getRole() : "user";

        Role role = roleService.findByName(queriedRole);

        boolean userExists = userService.userExistsInDatabase(userRegisterDTO.getEmail(), userRegisterDTO.getUserName());

        if (userExists) {
            throw new ResourceExistException("Email or username is already in use");
        }

        String keycloakUserId = keycloakService.createUserInKeycloak(userRegisterDTO);

        try {
            keycloakService.assignDefaultRoleToUser(keycloakUserId, role.getName());
            userService.saveUserToDatabase(userRegisterDTO, keycloakUserId);
            keycloakService.sendKeycloakVerifyEmail(keycloakUserId);
        } catch (KeycloakIntegrationException e) {
            log.error("Failed to create user in Keycloak {}. {}", keycloakUserId, e.getMessage());
            keycloakService.deleteUserFromKeycloak(keycloakUserId);
        }
    }

    @Override
    public AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException {
        validationHelper.validate(userLoginDTO);

        Optional<User> userFound = usersRepository.findByUserName(userLoginDTO.getUserName());
        if (userFound.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloakService.getClientId());
        form.add("client_secret", keycloakService.getClientSecret());
        form.add("username", userLoginDTO.getUserName());
        form.add("password", userLoginDTO.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakService.getServerToken(),
                    entity,
                    String.class
            );
            return objectMapper.readValue(response.getBody(), AccessTokenResponse.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Excepción {}", e.getMessage());
            throw new AuthenticationException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            throw new AuthenticationException("Login failed. Details: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public String loginWithProvider(String provider, String redirectUrl) {

        boolean isProviderEnabled = keycloakService.isProviderEnabled(provider);

        if (!isProviderEnabled) {
            throw new ResourceNotFoundException("Provider " + provider + " is not enabled");
        }

        return keycloakService.getRedirectProvider(provider, redirectUrl);
    }

    @Override
    public AccessTokenResponse handleAuthCallback(String code, String redirectUrl) throws JsonProcessingException {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", keycloakService.getClientId());
        form.add("client_secret", keycloakService.getClientSecret());
        form.add("code", code);
        form.add("redirect_uri", redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakService.getServerToken(),
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            return objectMapper.readValue(responseBody, AccessTokenResponse.class);

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new AuthenticationException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            log.error("Keycloak login failed: {}", e.getResponseBodyAsString());
            throw new AuthenticationException("Login failed. Details: " + e.getResponseBodyAsString());
        }
    }
}
