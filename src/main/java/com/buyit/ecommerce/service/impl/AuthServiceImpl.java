package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.exception.custom.KeycloakIntegrationException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.service.RoleService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final KeycloakService keycloakService;
    private final ValidationHelper validationHelper;

    @Value("${keycloak.email-verified}")
    private boolean emailVerified;

    @Override
    public void createUser(UserRegisterDTO userRegisterDTO) {

        validationHelper.validate(userRegisterDTO);

        String queriedRole = userRegisterDTO.getRole() != null ? userRegisterDTO.getRole() : "USER";

        Role role = roleService.findByName(queriedRole);

        boolean userExists = userService.userExistsInDatabase(userRegisterDTO.getEmail(), userRegisterDTO.getUserName());

        if (userExists) {
            throw new ResourceExistException("Email or username is already in use");
        }

        String keycloakUserId = keycloakService.createUserInKeycloak(userRegisterDTO);

        try {
            keycloakService.assignDefaultRoleToUser(keycloakUserId, role.getName());
            userService.saveUserToDatabase(userRegisterDTO, keycloakUserId);
            if (!emailVerified) {
                keycloakService.sendKeycloakVerifyEmail(keycloakUserId);
            }
        } catch (KeycloakIntegrationException e) {
            log.error("Failed to create user in Keycloak {}. {}", keycloakUserId, e.getMessage());
            keycloakService.deleteUserFromKeycloak(keycloakUserId);
        }
    }
}
