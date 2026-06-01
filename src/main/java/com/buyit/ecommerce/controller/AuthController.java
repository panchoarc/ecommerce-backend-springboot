package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final AuthService authService;

    @Public
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAPI<Void> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        authService.createUser(userRegisterDTO);
        return ResponseBuilder.success("User created successfully", null);
    }

    @GetMapping("/status")
    public ResponseAPI<Map<String, Boolean>> status(
            @AuthenticationPrincipal OidcUser user
    ) {
        Map<String, Boolean> authenticated = Map.of(
                "authenticated",
                user != null);
        return ResponseBuilder.success(null, authenticated);
    }
}
