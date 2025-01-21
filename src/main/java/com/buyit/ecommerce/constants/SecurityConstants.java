package com.buyit.ecommerce.constants;

import java.util.List;

public class SecurityConstants {

    private SecurityConstants() {
    }

    public static final List<String> PUBLIC_ROUTES = List.of("/actuator/**", "/v2/api-docs", "/v3/api-docs", "/redoc.html",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/auth/**");
}
