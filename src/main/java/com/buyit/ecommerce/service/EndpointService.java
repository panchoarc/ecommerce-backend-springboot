package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.request.endpoint.UpdateEndpointRequest;

import java.util.List;

public interface EndpointService {

    void syncEndpoints();

    void updateEndpoint(Long id, UpdateEndpointRequest endpointRequest);

    List<String> getPublicEndpoints();
}
