package com.buyit.ecommerce.dto.response.productAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductAttributesResponseDTO {

    private Long attributeId;
    private List<String> values;
}
