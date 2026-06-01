package com.buyit.ecommerce.dto.response.user;

public record UserSecurityStatusResponse(
        boolean otpEnabled,
        boolean webAuthnEnabled
) {
}