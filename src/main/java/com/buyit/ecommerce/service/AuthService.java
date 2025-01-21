package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.keycloak.representations.AccessTokenResponse;

public interface AuthService {

    void createUser(UserRegisterDTO userRegisterDTO);

    AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException;
}