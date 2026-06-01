package com.buyit.ecommerce.constants;

import java.util.List;

public class SecurityConstants {

    private SecurityConstants() {
    }

    public static final List<String> SWAGGER_URLS = List.of("/v2/api-docs", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**","/docs.html");

    public static final List<String> ACTUATOR_URLS =  List.of("/actuator/**");
}
