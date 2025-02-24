package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.endpoint.UpdateEndpointRequest;
import com.buyit.ecommerce.entity.Endpoint;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.repository.EndpointRepository;
import com.buyit.ecommerce.service.EndpointService;
import com.buyit.ecommerce.service.EndpointSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EndpointServiceImpl implements EndpointService {

    private final ApplicationContext applicationContext;
    private final EndpointRepository endpointRepository;
    private final EndpointSyncService endpointSyncService;

    @Override
    public void syncEndpoints() {
        Map<RequestMappingInfo, HandlerMethod> routes = getRoutes();
        Map<String, Endpoint> existingEndpoints = loadExistingEndpoints();
        endpointSyncService.processEndpoints(routes, existingEndpoints);

        log.info("✅ Endpoints synchronization completed.");
    }

    @Override
    public void updateEndpoint(Long id, UpdateEndpointRequest endpointRequest) {
        Endpoint endpoint = getEndpoint(id);
        endpoint.setIsPublic(endpointRequest.getIsPublic());
        endpoint.setIsActive(endpointRequest.getIsActive());
        endpointRepository.save(endpoint);
    }

    private Endpoint getEndpoint(Long id) {
        return endpointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("❌ Endpoint not found"));
    }

    private Map<RequestMappingInfo, HandlerMethod> getRoutes() {
        RequestMappingHandlerMapping mapping =
                applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        return mapping.getHandlerMethods();
    }

    private Map<String, Endpoint> loadExistingEndpoints() {
        return endpointRepository.findAll()
                .stream()
                .collect(Collectors.toMap(e -> e.getHttpMethod() + " " + e.getUrl(), e -> e));
    }
}

