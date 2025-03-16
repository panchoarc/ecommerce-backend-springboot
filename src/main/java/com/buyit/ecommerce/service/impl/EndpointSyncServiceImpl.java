package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.service.EndpointSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.*;
import java.util.stream.Collectors;

import static com.buyit.ecommerce.constants.SecurityConstants.PUBLIC_ROUTES;

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

        Map<Long, Endpoint> existingEndpointsById = existingEndpoints.values().stream()
                .collect(Collectors.toMap(Endpoint::getId, e -> e));

        existingEndpoints.forEach((key, endpoint) -> {
            if (!currentRoutes.contains(key) && Boolean.TRUE.equals(endpoint.getIsActive())) {
                deactivateEndpoint(endpoint, toDeactivate);
            } else if (currentRoutes.contains(key) && Boolean.FALSE.equals(endpoint.getIsActive())) {
                reactivateEndpoint(endpoint, toUpdate);
            }
        });

        currentRoutes.forEach(routeKey -> {
            if (!isPublicRoute(routeKey)) {
                String[] parts = routeKey.split(" ", 2);
                String method = parts[0];
                String path = parts[1];
                Map<String, String> pathParts = extractPathParts(path);
                Endpoint existingEndpoint = existingEndpoints.get(routeKey);

                if (existingEndpoint != null) {
                    updateEndpointIfNeeded(existingEndpoint, method, path, toUpdate);
                } else {
                    Optional<Endpoint> possibleMatch = existingEndpointsById.values().stream()
                            .filter(e -> e.getHttpMethod().equals(method) &&
                                    e.getBasePath().equals(pathParts.get("basePath")) &&
                                    e.getDynamicPath().equals(pathParts.get("dynamicPath")))
                            .findFirst();

                    if (possibleMatch.isPresent()) {
                        Endpoint matchedEndpoint = possibleMatch.get();
                        updateEndpointIfNeeded(matchedEndpoint, method, path, toUpdate);
                    } else {
                        createNewEndpoint(method, path, newEndpoints);
                    }
                }
            }
        });

        saveNewEndpoints(newEndpoints);
        updateExistingEndpoints(toUpdate);
        deactivateObsoleteEndpoints(toDeactivate);
    }

    private void deactivateEndpoint(Endpoint endpoint, List<Endpoint> toDeactivate) {
        endpoint.setIsActive(false);
        toDeactivate.add(endpoint);
    }

    private void reactivateEndpoint(Endpoint endpoint, List<Endpoint> toUpdate) {
        endpoint.setIsActive(true);
        toUpdate.add(endpoint);
    }

    private void updateEndpointIfNeeded(Endpoint endpoint, String method, String path, List<Endpoint> toUpdate) {
        boolean needsUpdate = false;

        Map<String, String> pathParts = extractPathParts(path);
        if (!endpoint.getHttpMethod().equals(method)) {
            endpoint.setHttpMethod(method);
            needsUpdate = true;
        }
        if (!endpoint.getUrl().equals(path)) {
            endpoint.setUrl(path);
            needsUpdate = true;
        }
        String basePath = pathParts.get("basePath");
        if (!endpoint.getBasePath().equals(basePath)) {
            endpoint.setBasePath(basePath);
            needsUpdate = true;
        }
        String dynamicPath = pathParts.get("dynamicPath");
        if (!Objects.equals(endpoint.getDynamicPath(), dynamicPath)) {
            endpoint.setDynamicPath(dynamicPath);
            needsUpdate = true;
        }
        if (needsUpdate) {
            toUpdate.add(endpoint);
        }
    }

    private void createNewEndpoint(String method, String path, List<Endpoint> newEndpoints) {

        Map<String, String> pathParts = extractPathParts(path);
        Endpoint newEndpoint = new Endpoint();
        newEndpoint.setHttpMethod(method);
        newEndpoint.setUrl(path);
        newEndpoint.setBasePath(pathParts.get("basePath"));
        newEndpoint.setDynamicPath(pathParts.get("dynamicPath"));
        newEndpoint.setIsActive(true);
        newEndpoint.setIsPublic(false);
        newEndpoints.add(newEndpoint);
    }

    private void saveNewEndpoints(List<Endpoint> newEndpoints) {
        if (!newEndpoints.isEmpty()) {
            endpointRepository.saveAll(newEndpoints);
            log.info("✅ Saved {} new endpoints.", newEndpoints.size());
        }
    }

    private void updateExistingEndpoints(List<Endpoint> toUpdate) {
        if (!toUpdate.isEmpty()) {
            endpointRepository.saveAll(toUpdate);
            log.info("🔄 Updated {} endpoints.", toUpdate.size());
        }
    }

    private void deactivateObsoleteEndpoints(List<Endpoint> toDeactivate) {
        if (!toDeactivate.isEmpty()) {
            endpointRepository.saveAll(toDeactivate);
            log.info("🚫 Deactivated {} obsolete endpoints.", toDeactivate.size());
        }
    }

    private Set<String> extractCurrentRoutes(Map<RequestMappingInfo, HandlerMethod> routes) {
        Set<String> currentRoutes = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : routes.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            Set<String> patterns = mappingInfo.getPatternValues();
            Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
            for (String pattern : patterns) {
                for (RequestMethod method : methods) {
                    String routeKey = method.name() + " " + pattern;
                    if (!isPublicRoute(pattern)) {
                        currentRoutes.add(routeKey);
                    }
                }
            }
        }
        return currentRoutes;
    }

    private Map<String, String> extractPathParts(String url) {
        String[] parts = url.split("/");
        String basePath = parts.length > 1 ? '/' + parts[1] : url;
        String dynamicPath = parts.length > 2 ? String.join("/", Arrays.copyOfRange(parts, 2, parts.length)) : "";

        Map<String, String> result = new HashMap<>();
        result.put("basePath", basePath);
        result.put("dynamicPath", dynamicPath);
        return result;
    }


    private boolean isPublicRoute(String url) {
        return PUBLIC_ROUTES.stream().anyMatch(publicRoute -> url.matches(convertToRegex(publicRoute)));
    }

    private String convertToRegex(String publicRoute) {
        return publicRoute.replace("**", ".*");
    }
}
