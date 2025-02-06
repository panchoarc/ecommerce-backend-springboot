package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.endpoint.UpdateEndpointRequest;
import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.service.EndpointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.buyit.ecommerce.constants.SecurityConstants.PUBLIC_ROUTES;

@Service
@RequiredArgsConstructor
@Slf4j
public class EndpointServiceImpl implements EndpointService {

    private final ApplicationContext applicationContext;
    private final EndpointRepository endpointRepository;

    @Override
    public void syncEndpoints() {
        Map<RequestMappingInfo, HandlerMethod> routes = getRoutes();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : routes.entrySet()) {
            processRoute(entry.getKey(), entry.getValue());
        }

        log.info("Endpoints synchronization completed.");
    }

    @Override
    public void updateEndpoint(Long id, UpdateEndpointRequest endpointRequest) {

        Endpoint updatedEndpoint = getEndpoint(id);
        updatedEndpoint.setIsPublic(endpointRequest.getIsPublic());
        updatedEndpoint.setIsActive(endpointRequest.getIsActive());

        endpointRepository.save(updatedEndpoint);
    }

    public Endpoint getEndpoint(Long id) {

        return endpointRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Endpoint not found"));
    }

    private Map<RequestMappingInfo, HandlerMethod> getRoutes() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        return mapping.getHandlerMethods();
    }

    private void processRoute(RequestMappingInfo info, HandlerMethod handlerMethod) {
        Set<String> patterns = info.getPatternValues();
        Set<RequestMethod> methods = info.getMethodsCondition().getMethods();

        for (String pattern : patterns) {
            for (RequestMethod method : methods) {
                saveEndpointIfNotExists(pattern, handlerMethod, method);
            }
        }
    }

    private void saveEndpointIfNotExists(String pattern, HandlerMethod handlerMethod, RequestMethod method) {
        // Log opcional para depuración
        //log.info("Processing endpoint {} with HTTP method {}", pattern, method);

        String[] extractPaths = extractPaths(pattern);
        String basePath = extractPaths[0];
        String dynamicPath = extractPaths.length > 1 ? extractPaths[1] : null;

        boolean isPublic = isPublicRoute(pattern);
        log.info("Extracted isPublic: {}", isPublic);

        if (!isPublic) {
            // Verificar si el endpoint ya existe en la base de datos usando URL y HTTP method
            Optional<Endpoint> existingEndpoint = endpointRepository.findByUrlAndHttpMethod(pattern, method.name());

            if (existingEndpoint.isEmpty()) {
                // Si el endpoint no existe con este patrón y método, lo creamos
                createAndSaveNewEndpoint(pattern, handlerMethod, method);
            } else {
                // Si el endpoint ya existe, verificamos si alguna propiedad ha cambiado
                Endpoint existing = existingEndpoint.get();

                existing.setUrl(pattern);  // Actualizamos la URL completa
                existing.setBasePath(basePath);
                existing.setDynamicPath(dynamicPath);
                existing.setHttpMethod(method.name());
                existing.setMethodName(handlerMethod.getMethod().getName());
                endpointRepository.saveAndFlush(existing);

                // Usamos saveAndFlush para asegurar que se guarda correctamente
                //log.info("Endpoint actualizado: URL = {}, HTTP Method = {}, Method Name = {}", pattern, method, handlerMethod.getMethod().getName());
            }
        }


    }

    private String[] extractPaths(String pattern) {
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1);  // Elimina la primera barra
        }
        return pattern.split("/", 2);  // Divide solo en 2 partes
    }


    private void createAndSaveNewEndpoint(String pattern, HandlerMethod handlerMethod, RequestMethod method) {

        String[] extractPaths = extractPaths(pattern);
        log.info("Extracted paths: {}", Arrays.toString(extractPaths));
        String basePath = extractPaths[0];
        String dynamicPath = extractPaths.length > 1 ? extractPaths[1] : null;

        Endpoint newEndpoint = new Endpoint();
        newEndpoint.setUrl(pattern);
        newEndpoint.setBasePath(basePath);
        newEndpoint.setDynamicPath(dynamicPath);
        newEndpoint.setMethodName(handlerMethod.getMethod().getName()); // Nombre del método del controlador
        newEndpoint.setHttpMethod(method.name()); // Método HTTP (GET, POST, etc.)
        newEndpoint.setIsPublic(false); // Verificar si es pública
        newEndpoint.setIsActive(true);
        endpointRepository.save(newEndpoint);
        log.info("Endpoint created: URL = {}, Method = {}", pattern, method);
    }

    private boolean isPublicRoute(String url) {
        // Usamos un Set para una búsqueda más eficiente
        return PUBLIC_ROUTES.stream()
                .anyMatch(publicRoute -> url.matches(convertToRegex(publicRoute)));
    }

    private String convertToRegex(String publicRoute) {
        return publicRoute.replace("**", ".*");
    }
}
