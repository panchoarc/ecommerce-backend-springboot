package com.buyit.ecommerce.dto.request.endpoint;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateEndpointRequest {

    @JsonProperty("is_public")
    @NotNull(message = "is_public needs a boolean value")
    private Boolean isPublic;

    @JsonProperty("is_active")
    @NotNull(message = "is_active needs a boolean value")
    private Boolean isActive;
}
