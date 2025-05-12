package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.keycloak.representations.AccessTokenResponse;

public interface AuthService {

    void createUser(UserRegisterDTO userRegisterDTO);

    AccessTokenResponse login(UserLoginDTO userLoginDTO) throws JsonProcessingException;

    String loginWithProvider(String provider,String redirectUrl);

    AccessTokenResponse handleAuthCallback(String code, String redirectUrl) throws JsonProcessingException;


    AccessTokenResponse refreshAccessToken(String refreshToken);
}