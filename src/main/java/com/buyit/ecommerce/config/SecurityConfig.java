package com.buyit.ecommerce.config;

import com.buyit.ecommerce.security.PublicEndpointMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static com.buyit.ecommerce.constants.SecurityConstants.ACTUATOR_URLS;
import static com.buyit.ecommerce.constants.SecurityConstants.SWAGGER_URLS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {


    private final PublicEndpointMatcher publicEndpointMatcher;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/oauth2/**", "/login/**", "/auth/**").permitAll()
                        .requestMatchers(SWAGGER_URLS.toArray(new String[0])).permitAll()
                        .requestMatchers(ACTUATOR_URLS.toArray(new String[0])).permitAll()
                        .requestMatchers(publicEndpointMatcher).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}