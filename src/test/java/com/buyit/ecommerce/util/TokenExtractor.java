package com.buyit.ecommerce.util;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class TokenExtractor {

    private final AuthService authService;


    public String extractTokenFromUser(String username, String password) throws JsonProcessingException {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserName(username);
        userLoginDTO.setPassword(password);

        AccessTokenResponse response = authService.login(userLoginDTO);
        return response.getToken();
    }
}
