package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.response.user.UserSecurityStatusResponse;
import com.buyit.ecommerce.service.KeycloakService;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class UserSecurityController {

    private final KeycloakService keycloakService;

    @GetMapping("/status")
    public ResponseAPI<UserSecurityStatusResponse> status(
            @AuthenticationPrincipal Jwt user
    ) {

        if (user == null) {
            return ResponseBuilder.success(
                    null,
                    new UserSecurityStatusResponse(
                            false, // otpEnabled
                            false  // authenticated
                    )
            );
        }
        String userId = user.getSubject();

        return ResponseBuilder.success(
                null,
                new UserSecurityStatusResponse(
                        keycloakService.hasOTP(userId),
                        true
                )
        );
    }

    @PostMapping("/start-otp-setup")
    public void startOtpSetup(@AuthenticationPrincipal Jwt user) {

        String userId = user.getSubject();
        keycloakService.startOTPSetup(userId);
    }

    @DeleteMapping("/otp")
    public void disableOtp(@AuthenticationPrincipal Jwt user) {

        String userId = user.getSubject();
        keycloakService.disableOTP(userId);
    }

    @GetMapping("/sessions")
    public void getUserSessions(@AuthenticationPrincipal Jwt user) {

        String userId = user.getSubject();
        keycloakService.getUserSessions(userId);
    }
}
