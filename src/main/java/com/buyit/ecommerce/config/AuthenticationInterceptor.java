package com.buyit.ecommerce.config;

import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.UnAuthorizedException;
import com.buyit.ecommerce.repository.EndpointRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Value("${keycloak.client-id}")
    private String clientId;

    private final JwtDecoder jwtDecoder;
    private final EndpointRepository endpointRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String templatePattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String method = request.getMethod();

        if (isPublicEndpoint(templatePattern, method)) {
            request.setAttribute("IS_PUBLIC_ENDPOINT", true);
            return true;
        }

        Collection<String> userRoles = extractUserRoles(request);
        request.setAttribute("USER_ROLES", userRoles);

        return true;
    }

    private Collection<String> extractUserRoles(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnAuthorizedException("Authorization header is missing or invalid.");
        }

        token = token.substring(7);
        Jwt decodedToken = jwtDecoder.decode(token);

        Map<String, Object> resourceAccess = decodedToken.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(clientId);
            return (Collection<String>) clientRoles.get("roles");
        }
        throw new UnAuthorizedException("User roles not found in token.");
    }

    private boolean isPublicEndpoint(String uri, String method) {
        Optional<Endpoint> isPublicUrl = endpointRepository.findByUrlAndHttpMethod(uri, method);
        return isPublicUrl.isPresent() && Boolean.TRUE.equals(isPublicUrl.get().getIsPublic());
    }

}
