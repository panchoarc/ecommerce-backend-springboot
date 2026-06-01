package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.anotations.RequirePermission;
import com.buyit.ecommerce.constants.PermissionsConstants;
import com.buyit.ecommerce.dto.response.user.UserInfoResponse;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @RequirePermission(value = PermissionsConstants.USERS_MY_PROFILE)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.USERS_MY_PROFILE + "')")
    @GetMapping("/me")
    public ResponseAPI<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String username = jwt.getClaimAsString("preferred_username");

        Object rolesObj = jwt.getClaims().get("roles");

        List<String> roles = new ArrayList<>();

        if (rolesObj instanceof List<?> list) {
            roles = list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .filter(r -> r.startsWith("APP_"))
                    .map(r -> r.substring(4))
                    .toList();
        }

        UserInfoResponse response = new UserInfoResponse();
        response.setEmail(email);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setUserName(username);
        response.setRoles(roles);

        return ResponseBuilder.success("User found successfully", response);
    }
}
