package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.service.EndpointSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EndpointSyncServiceImpl implements EndpointSyncService {

    private final EndpointRepository endpointRepository;

    @Override
    public void processEndpoints(Map<RequestMappingInfo, HandlerMethod> routes, Map<String, Endpoint> existingEndpoints) {
        Set<String> currentRoutes = extractCurrentRoutes(routes);
        List<Endpoint> newEndpoints = new ArrayList<>();
        List<Endpoint> toUpdate = new ArrayList<>();
        List<Endpoint> toDeactivate = new ArrayList<>();

        // Desactivar endpoints obsoletos
        existingEndpoints.values().forEach(endpoint -> {
            String routeKey = endpoint.getHttpMethod() + " " + endpoint.getUrl();
            if (!currentRoutes.contains(routeKey)) {
                deactivateIfActive(endpoint, toDeactivate);
            }
        });

        // Procesar nuevos endpoints y actualizaciones
        routes.forEach((info, handler) -> {
            for (String pattern : info.getPatternValues()) {
                for (String method : info.getMethodsCondition().getMethods().stream().map(Enum::name).toList()) {
                    String routeKey = method + " " + pattern;
                    boolean isPublic = isPublicEndpoint(handler);

                    if (!existingEndpoints.containsKey(routeKey)) {
                        newEndpoints.add(createNewEndpoint(routeKey, isPublic));
                    } else {
                        Endpoint existingEndpoint = existingEndpoints.get(routeKey);
                        if (hasChanges(existingEndpoint, method, pattern, isPublic)) {
                            updateEndpoint(existingEndpoint, method, pattern, isPublic);
                            toUpdate.add(existingEndpoint);
                        }
                    }
                }
            }
        });

        // Guardar cambios en la base de datos
        saveEndpoints(newEndpoints, "Saved {} new endpoints.");
        saveEndpoints(toUpdate, "Updated {} endpoints.");
        saveEndpoints(toDeactivate, "Deactivated {} obsolete endpoints.");
    }

    private boolean hasChanges(Endpoint endpoint, String method, String url, boolean isPublic) {
        return !endpoint.getHttpMethod().equals(method) ||
                !endpoint.getUrl().equals(url) ||
                !endpoint.getBasePath().equals(extractBasePath(url)) ||
                !endpoint.getDynamicPath().equals(extractDynamicPath(url)) ||
                !Objects.equals(endpoint.getIsPublic(), isPublic) || // Detecta cambios en `isPublic`
                Boolean.FALSE.equals(endpoint.getIsActive()); // Reactiva si estaba desactivado
    }

    private void updateEndpoint(Endpoint endpoint, String method, String url, boolean isPublic) {
        endpoint.setHttpMethod(method);
        endpoint.setUrl(url);
        endpoint.setBasePath(extractBasePath(url));
        endpoint.setDynamicPath(extractDynamicPath(url));
        endpoint.setIsPublic(isPublic); // Ahora actualiza si cambia `isPublic`
        endpoint.setIsActive(true);
    }

    private void deactivateIfActive(Endpoint endpoint, List<Endpoint> toDeactivate) {
        if (Boolean.TRUE.equals(endpoint.getIsActive())) {
            endpoint.setIsActive(false);
            toDeactivate.add(endpoint);
        }
    }

    private Endpoint createNewEndpoint(String routeKey, boolean isPublic) {
        String[] parts = routeKey.split(" ", 2);
        Endpoint endpoint = new Endpoint();
        endpoint.setHttpMethod(parts[0]);
        endpoint.setBasePath(extractBasePath(parts[1]));
        endpoint.setDynamicPath(extractDynamicPath(parts[1]));
        endpoint.setUrl(parts[1]);
        endpoint.setIsActive(true);
        endpoint.setIsPublic(isPublic);
        return endpoint;
    }

    private void saveEndpoints(List<Endpoint> endpoints, String message) {
        if (!endpoints.isEmpty()) {
            endpointRepository.saveAll(endpoints);
            log.info(message, endpoints.size());
        }
    }

    private Set<String> extractCurrentRoutes(Map<RequestMappingInfo, HandlerMethod> routes) {
        return routes.entrySet().stream()
                .flatMap(entry -> entry.getKey().getPatternValues().stream()
                        .flatMap(pattern -> entry.getKey().getMethodsCondition().getMethods().stream()
                                .map(method -> method.name() + " " + pattern)))
                .collect(Collectors.toSet());
    }

    private boolean isPublicEndpoint(HandlerMethod handlerMethod) {
        boolean classIsPublic = handlerMethod.getBeanType().isAnnotationPresent(Public.class);
        boolean methodIsPublic = handlerMethod.getMethod().isAnnotationPresent(Public.class);

        // 🚀 Nueva verificación: si el método tiene restricciones, NO debe ser público
        boolean methodHasSecurity = handlerMethod.getMethod().isAnnotationPresent(PreAuthorize.class) ||
                handlerMethod.getMethod().isAnnotationPresent(Secured.class);

        return (classIsPublic || methodIsPublic) && !methodHasSecurity;
    }

    private String extractBasePath(String url) {
        String[] parts = url.split("/");
        return parts.length > 1 ? '/' + parts[1] : url;
    }

    private String extractDynamicPath(String url) {
        String[] parts = url.split("/");
        return parts.length > 2 ? String.join("/", Arrays.copyOfRange(parts, 2, parts.length)) : "";
    }
}
