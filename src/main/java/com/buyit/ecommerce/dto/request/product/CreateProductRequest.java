package com.buyit.ecommerce.dto.request.product;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "name cannot be blank")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "description cannot be blank")
    @Size(min = 1, max = 1000, message = "description has to be between 1 and 1000 characters")
    @JsonProperty("description")
    private String description;

    @Positive(message = "price could not be lower than 0")
    @JsonProperty("price")
    private BigDecimal price;

    @Positive(message = "quantity could not be lower than 0cvb ")
    @JsonProperty("quantity")
    private int quantity;

    @NotNull(message = "You need to add categories")
    @JsonProperty("category_ids")
    private List<Long> categoryIds;
}
