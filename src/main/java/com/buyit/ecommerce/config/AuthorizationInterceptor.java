package com.buyit.ecommerce.config;

import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.exception.custom.UnAuthorizedException;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.service.PermissionsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.buyit.ecommerce.constants.SecurityConstants.PUBLIC_ROUTES;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Value("${keycloak.client-id}")
    private String clientId;

    private final JwtDecoder jwtDecoder;
    private final PermissionsService permissionService;
    private final EndpointRepository endpointRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String mappedPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            if (mappedPattern == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot determine request mapping");
                return false;
            }
            String method = request.getMethod();

            log.info("Checking access for endpoint: {} with method: {}", mappedPattern, method);

            if (isPublicEndpoint(mappedPattern, method)) {
                return true;
            }

            Collection<String> userRoles = extractUserRoles(request);

            String normalizedUrl = normalizeUrl(mappedPattern);

            if (userRoles.isEmpty() || !permissionService.hasAccess(normalizedUrl, method, userRoles)) {
                throw new DeniedAccessException("You cannot access this resource");
            }

            return true;
        } catch (UnAuthorizedException | DeniedAccessException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
            return false;
        }
    }

    private String normalizeUrl(String requestUri) {
        return requestUri.replaceAll("\\{[a-zA-Z0-9]+}", "[^/]+");
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

    private Collection<String> extractUserRoles(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnAuthorizedException("Missing or invalid Authorization header");
        }

        token = token.substring(7);
        Jwt decodedToken = jwtDecoder.decode(token);

        Map<String, Object> resourceAccess = decodedToken.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(clientId);
            return (Collection<String>) clientRoles.get("roles");
        }
        throw new UnAuthorizedException("User roles not found in token");
    }

    private String convertToRegex(String publicRoute) {
        return publicRoute.replace("**", ".*");
    }
}
