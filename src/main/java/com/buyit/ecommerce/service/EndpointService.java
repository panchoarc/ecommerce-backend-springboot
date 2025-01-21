package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.request.endpoint.UpdateEndpointRequest;

public interface EndpointService {

    void syncEndpoints();

    void updateEndpoint(Long id, UpdateEndpointRequest endpointRequest);
}
