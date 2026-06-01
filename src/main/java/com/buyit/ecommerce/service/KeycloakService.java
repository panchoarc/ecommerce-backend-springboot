package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

public interface KeycloakService {


    void getUserSessions(String userId);

    List<RoleRepresentation> getClientRoles();
    String createUserInKeycloak(UserRegisterDTO userRegisterDTO);

    void assignDefaultRoleToUser(String keycloakUserId, String roleName);

    void sendKeycloakVerifyEmail(String keycloakId);


    void deleteUserFromKeycloak(String userId);


    boolean hasOTP(String userId);
    void startOTPSetup(String userId);
    void disableOTP(String userId);

}
