package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.endpoint.UpdateEndpointRequest;
import com.buyit.ecommerce.service.EndpointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/endpoints")
@RequiredArgsConstructor
public class EndpointController {


    private final EndpointService endpointService;

    @PostMapping("/sync")
    public void syncEndpoints() {
        endpointService.syncEndpoints();
    }

    @PutMapping("/{id}")
    public void editEndpoint(@PathVariable("id") Long id, @RequestBody @Valid UpdateEndpointRequest endpointRequest) {
        endpointService.updateEndpoint(id, endpointRequest);

    }
}
