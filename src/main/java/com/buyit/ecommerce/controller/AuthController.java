package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        authService.createUser(userRegisterDTO);
        return ResponseBuilder.success("User created successfully", null);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AccessTokenResponse> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) throws JsonProcessingException {
        AccessTokenResponse token = authService.login(userLoginDTO);
        return ResponseBuilder.success("Login successfully", token);
    }

    @GetMapping("/provider/{provider}")
    @ResponseStatus(HttpStatus.FOUND)
    public ApiResponse<String> loginWithProvider(@PathVariable String provider, HttpServletRequest request) {
        String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/auth/callback";

        String authUrl = authService.loginWithProvider(provider, redirectUri);
        return ResponseBuilder.success("URL", authUrl);
    }

    @GetMapping("/callback")
    public ApiResponse<AccessTokenResponse> handleCallback(@RequestParam("code") String code, HttpServletRequest request) throws JsonProcessingException {

        String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/auth/callback";

        AccessTokenResponse response = authService.handleAuthCallback(code, redirectUri);

        return ResponseBuilder.success("Login successfully", response);
    }
}
