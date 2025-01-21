package com.buyit.ecommerce.dto.request.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {

    @NotBlank
    @JsonProperty("city")
    private String city;

    @NotBlank
    @JsonProperty("street")
    private String street;

    @NotBlank
    @JsonProperty("postal_code")
    private String postalCode;

    @NotBlank
    @JsonProperty("country")
    private String country;

}
