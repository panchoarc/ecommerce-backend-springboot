package com.buyit.ecommerce.service;

import com.buyit.ecommerce.entity.Endpoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map;

public interface EndpointSyncService {

    void processEndpoints(Map<RequestMappingInfo, HandlerMethod> routes, Map<String, Endpoint> existingEndpoints);
}
