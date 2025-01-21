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
        log.info("Processing endpoint {} with HTTP method {}", pattern, method);

        // Verificar si el endpoint ya existe en la base de datos usando URL y HTTP method
        Optional<Endpoint> existingEndpoint = endpointRepository.findByUrlAndHttpMethod(pattern, method.name());

        if (existingEndpoint.isEmpty()) {
            // Si el endpoint no existe con este patrón y método, lo creamos
            createAndSaveNewEndpoint(pattern, handlerMethod, method);
        } else {
            // Si el endpoint ya existe, verificamos si alguna propiedad ha cambiado
            Endpoint existing = existingEndpoint.get();

            boolean isUpdated = false;

            // Comparar las partes de las URLs
            if (!existing.getUrl().equals(handlerMethod.getMethod().getName())) {
                existing.setUrl(pattern);  // Actualizamos la URL completa
                isUpdated = true;
            }

            // Comparar y actualizar otras propiedades (método HTTP, nombre del método)
            if (!existing.getHttpMethod().equals(method.name())) {
                existing.setHttpMethod(method.name());
                isUpdated = true;
            }

            if (!existing.getMethodName().equals(handlerMethod.getMethod().getName())) {
                existing.setMethodName(handlerMethod.getMethod().getName());
                isUpdated = true;
            }

            // Si hubo cambios, guardamos el endpoint actualizado
            if (isUpdated) {
                endpointRepository.saveAndFlush(existing);  // Usamos saveAndFlush para asegurar que se guarda correctamente
                log.info("Endpoint actualizado: URL = {}, HTTP Method = {}, Method Name = {}", pattern, method, handlerMethod.getMethod().getName());
            } else {
                log.info("No se detectaron cambios para el endpoint: URL = {}, HTTP Method = {}", pattern, method);
            }
        }
    }


    private void createAndSaveNewEndpoint(String pattern, HandlerMethod handlerMethod, RequestMethod method) {
        Endpoint newEndpoint = new Endpoint();
        newEndpoint.setUrl(pattern);
        newEndpoint.setMethodName(handlerMethod.getMethod().getName()); // Nombre del método del controlador
        newEndpoint.setHttpMethod(method.name()); // Método HTTP (GET, POST, etc.)
        newEndpoint.setIsPublic(isPublicRoute(pattern)); // Verificar si es pública
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
        return publicRoute.replace("**", ".*").replace("*", "[^/]*");
    }
}
