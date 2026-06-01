package com.buyit.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        List<String> roles = realmAccess == null
                ? List.of()
                : (List<String>) realmAccess.get("roles");

        return roles.stream()
                .filter(role ->
                        !role.startsWith("offline_") &&
                                !role.startsWith("uma_") &&
                                !role.startsWith("default-roles"))
                .map(role -> role.replace("APP_", "")) // 🔥 limpieza negocio
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}