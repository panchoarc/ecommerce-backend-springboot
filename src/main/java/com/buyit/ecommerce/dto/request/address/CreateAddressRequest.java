package com.buyit.ecommerce.dto.request.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotBlank(message = "alias cannot be blank")
    @JsonProperty("alias")
    private String alias;

    @NotBlank(message = "city cannot be blank")
    @JsonProperty("city")
    private String city;

    @NotBlank(message = "country cannot be blank")
    @JsonProperty("country")
    private String country;

    @NotBlank(message = "postal_code cannot be blank")
    @JsonProperty("postal_code")
    private String postalCode;

    @NotBlank(message = "street cannot be blank")
    @JsonProperty("street")
    private String street;
}
