package com.buyit.ecommerce.dto.response.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressResponse {

    private Long id;
    private String street;
    private String city;
    private String country;
    private String postalCode;
}
