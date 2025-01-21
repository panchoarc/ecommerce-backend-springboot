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
    @NotNull(message = "Cannot be null")
    private Boolean isPublic;

    @JsonProperty("is_active")
    @NotNull(message = "Cannot be null")
    private Boolean isActive;
}
