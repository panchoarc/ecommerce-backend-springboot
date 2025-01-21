package com.buyit.ecommerce.dto.request.product;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest implements Serializable {

    @NotBlank(message = "Product name")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Product description")
    @Size(min = 1, max = 1000, message = "Product has to get between 1 and 1000 characters")
    @JsonProperty("description")
    private String description;

    @Positive(message = "Product price could not be lower than 0")
    @JsonProperty("price")
    private BigDecimal price;

    @Positive(message = "Product quantity could not be lower than 0cvb ")
    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("category_ids")
    private List<Long> categoryIds;
}
