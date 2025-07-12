package com.buyit.ecommerce.dto.request.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "name cannot be blank")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "description cannot be blank")
    @JsonProperty("description")
    private String description;

    @Positive(message = "price could not be lower than 0")
    @JsonProperty("price")
    private BigDecimal price;

    @Positive(message = "quantity could not be lower than 0")
    @JsonProperty("quantity")
    private int quantity;

    @NotNull(message = "is_active needs to be a boolean value")
    @JsonProperty("is_active")
    private Boolean isActive;

    @NotNull(message = "category_id needs at least one category")
    @JsonProperty("category_id")
    private Long categoryId;
}
