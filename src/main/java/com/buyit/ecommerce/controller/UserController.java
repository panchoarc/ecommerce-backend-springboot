package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.dto.response.user.UserInfoResponse;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final KeycloakService keycloakService;

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        String username = jwt.getClaimAsString("preferred_username");
        List<String> userRoles = extractRolesFromUser(jwt);


        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setEmail(email);
        userInfoResponse.setFirstName(firstName);
        userInfoResponse.setLastName(lastName);
        userInfoResponse.setUserName(username);
        userInfoResponse.setRoles(userRoles);

        return ResponseBuilder.success("User found successfully", userInfoResponse);
    }


    private List<String> extractRolesFromUser(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        Map<String, Object> buyitClientAccess = (Map<String, Object>) resourceAccess.get(keycloakService.getClientId());

        List<String> roles = new ArrayList<>();
        if (buyitClientAccess != null && buyitClientAccess.containsKey("roles")) {
            roles = (List<String>) buyitClientAccess.get("roles");
        }

        return roles;
    }
}
