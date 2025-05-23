package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    User getUserByKeycloakId(String keycloakId);

    String extractKeycloakIdFromUser(Jwt user);

    void saveUserToDatabase(UserRegisterDTO userDTO, String keycloakId);

    boolean userExistsInDatabase(String email, String username);
}

