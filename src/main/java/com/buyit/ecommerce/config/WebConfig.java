package com.buyit.ecommerce.config;

import com.buyit.ecommerce.service.EndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.buyit.ecommerce.constants.SecurityConstants.ACTUATOR_URLS;
import static com.buyit.ecommerce.constants.SecurityConstants.SWAGGER_URLS;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${frontend.url}")
    private String frontendUrl;

    private final AuthorizationInterceptor authorizationInterceptor;
    private final AuthenticationInterceptor authenticationInterceptor;

    private final EndpointService endpointService;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        List<String> publicEndpoints = endpointService.getPublicEndpoints();

        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(SWAGGER_URLS)
                .excludePathPatterns(ACTUATOR_URLS)
                .order(1);

        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(publicEndpoints.toArray(new String[0]))
                .excludePathPatterns(SWAGGER_URLS)
                .excludePathPatterns(ACTUATOR_URLS)
                .order(2);

    }


}