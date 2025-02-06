package com.buyit.ecommerce.config;

import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.exception.custom.UnAuthorizedException;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.service.PermissionsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.buyit.ecommerce.constants.SecurityConstants.PUBLIC_ROUTES;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicRoleFilter extends OncePerRequestFilter {

    @Value("${keycloak.client-id}")
    private String clientId;

    private final JwtDecoder jwtDecoder;
    private final PermissionsService permissionService;
    private final EndpointRepository endpointRepository;


    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // Procesar el endpoint y normalizarlo
            String requestUri = request.getRequestURI();
            String endpoint = requestUri.substring(request.getContextPath().length());

            String method = request.getMethod();

            log.info("Filtering outside publicEndpoint {} {}", method, endpoint);

            // Verificar si el endpoint es público
            if (isPublicEndpoint(endpoint, method)) {
                filterChain.doFilter(request, response);
                return;
            }
            String normalizedUrl = normalizeUrl(endpoint);

            // Validar autenticación y roles
            Collection<String> userRoles = extractUserRoles(request);
            boolean containsAdmin = userRoles.contains("admin");

            if (containsAdmin) {
                filterChain.doFilter(request, response);
                return;
            }

            if (userRoles.isEmpty() || !permissionService.hasAccess(normalizedUrl, method, userRoles)) {
                throw new DeniedAccessException("You cannot access this resource");
            }

            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);

        } catch (UnAuthorizedException | DeniedAccessException ex) {
            // Aquí no es necesario responder con sendError
            // Solo lanzamos las excepciones para que el manejo global de excepciones las capture
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    private String normalizeUrl(String requestUri) {
        return requestUri.replaceAll("\\d+", "{id}");
    }

    private boolean isPublicEndpoint(String uri, String method) {

        log.info("Filtering insidePublicEndpoint {} {}", method, uri);
        // Verificar si la URL está en las rutas públicas definidas en SECURITY_CONSTANTS
        boolean isPublicRoute = PUBLIC_ROUTES.stream()
                .anyMatch(publicRoute -> uri.matches(convertToRegex(publicRoute)));


        log.info("isPublicRoute: {}", isPublicRoute);
        if (isPublicRoute) {
            return true;  // Si la ruta está en las rutas públicas predefinidas, la dejamos pasar
        }

        log.info("normalizedUrl: {}", uri);
        // Verificar si la URL es pública en la base de datos
        Optional<Endpoint> isPublicUrl = endpointRepository.findByUrlAndHttpMethod(uri, method);
        return isPublicUrl.isPresent() && Boolean.TRUE.equals(isPublicUrl.get().getIsPublic());
    }

    private Collection<String> extractUserRoles(HttpServletRequest request) {
        // Verificar autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnAuthorizedException("Not authorized");
        }

        // Obtener el token del encabezado
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnAuthorizedException("Not authorized");
        }

        token = token.substring(7);
        Jwt decodedToken = jwtDecoder.decode(token);

        // Extraer roles del token
        Map<String, Object> resourceAccess = decodedToken.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(clientId);
            return (Collection<String>) clientRoles.get("roles");
        }

        throw new UnAuthorizedException("User roles not found in token");
    }

    private String convertToRegex(String publicRoute) {
        return publicRoute.replace("**", ".*").replace("*", ".*");
    }
}
