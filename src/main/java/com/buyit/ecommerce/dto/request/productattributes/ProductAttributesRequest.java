package com.buyit.ecommerce.dto.request.productattributes;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductAttributesRequest {

    private Long attributeId;
    private List<String> values;
}
