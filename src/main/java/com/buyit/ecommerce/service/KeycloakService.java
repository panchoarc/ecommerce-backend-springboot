package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

public interface KeycloakService {

    List<RoleRepresentation> getClientRoles();

    String getClientInternalId();

    String getClientId();

    String getClientSecret();

    String createUserInKeycloak(UserRegisterDTO userRegisterDTO);

    void assignDefaultRoleToUser(String keycloakUserId, String roleName);

    void sendKeycloakVerifyEmail(String keycloakId);

    String getServerToken();

    String getServerUrl();
}
