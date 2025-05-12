package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.dto.request.UserLoginDTO;
import com.buyit.ecommerce.dto.request.UserRegisterDTO;
import com.buyit.ecommerce.exception.custom.AuthenticationException;
import com.buyit.ecommerce.service.AuthService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final AuthService authService;

    @Public
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        authService.createUser(userRegisterDTO);
        return ResponseBuilder.success("User created successfully", null);
    }

    @Public
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AccessTokenResponse> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) throws JsonProcessingException {
        AccessTokenResponse token = authService.login(userLoginDTO);

        Cookie cookie = new Cookie("refresh_token", token.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
        token.setRefreshToken(null);

        return ResponseBuilder.success("Login successfully", token);
    }

    @Public
    @GetMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AccessTokenResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("Missing refresh token"));

        AccessTokenResponse token = authService.refreshAccessToken(refreshToken);

        // Actualizamos la cookie con el nuevo refresh_token
        Cookie cookie = new Cookie("refresh_token", token.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");

        int maxAge = (int) Duration.ofMinutes(30).getSeconds();
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

        token.setRefreshToken(null);
        return ResponseBuilder.success("Token refreshed", token);
    }

    @Public
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // elimina la cookie
        response.addCookie(cookie);
    }


    @Public
    @GetMapping("/provider/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<String> loginWithProvider(@PathVariable String provider, HttpServletRequest request) {
        String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/auth/callback";

        String authUrl = authService.loginWithProvider(provider, redirectUri);
        return ResponseBuilder.success("URL", authUrl);
    }

    @Public
    @GetMapping("/callback")
    public void handleCallback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/auth/callback";
        AccessTokenResponse tokenResponse = authService.handleAuthCallback(code, redirectUri);

        String callbackUrl = frontendUrl + "/auth-callback.html"
                + "?token=" + tokenResponse.getToken()
                + "&refresh_token=" + tokenResponse.getRefreshToken();

        response.sendRedirect(callbackUrl);
    }
}
