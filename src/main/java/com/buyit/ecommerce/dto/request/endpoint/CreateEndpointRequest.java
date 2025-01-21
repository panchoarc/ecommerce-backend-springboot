package com.buyit.ecommerce.dto.request.endpoint;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CreateEndpointRequest {

    @JsonProperty("endpoint_ids")
    private List<Long> endpointIds;
}
