package com.buyit.ecommerce.config;

import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.UnAuthorizedException;
import com.buyit.ecommerce.repository.EndpointRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.buyit.ecommerce.constants.SecurityConstants.PUBLIC_ROUTES;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${keycloak.client-id}")
    private String clientId;

    private final JwtDecoder jwtDecoder;
    private final EndpointRepository endpointRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestedUri = request.getRequestURI();
        String endpoint = requestedUri.substring(request.getContextPath().length());
        String method = request.getMethod();

        if (isPublicEndpoint(endpoint, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        Collection<String> userRoles = extractUserRoles(request);

        request.setAttribute("USER_ROLES", userRoles);

        filterChain.doFilter(request, response);

    }


    private Collection<String> extractUserRoles(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        token = token.substring(7);
        Jwt decodedToken = jwtDecoder.decode(token);

        Map<String, Object> resourceAccess = decodedToken.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(clientId);
            return (Collection<String>) clientRoles.get("roles");
        }
        throw new UnAuthorizedException("User roles not found in token");
    }

    private boolean isPublicEndpoint(String uri, String method) {
        boolean isPublicRoute = PUBLIC_ROUTES.stream()
                .anyMatch(publicRoute -> uri.matches(convertToRegex(publicRoute)));

        if (isPublicRoute) {
            return true;
        }

        Optional<Endpoint> isPublicUrl = endpointRepository.findByUrlAndHttpMethod(uri, method);
        return isPublicUrl.isPresent() && Boolean.TRUE.equals(isPublicUrl.get().getIsPublic());
    }

    private String convertToRegex(String publicRoute) {
        return publicRoute.replace("**", ".*");
    }
}
