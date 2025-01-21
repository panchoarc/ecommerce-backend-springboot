package com.buyit.ecommerce.dto.request.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderRequest {

    @NotNull
    @JsonProperty("product_id")
    private Long productId;

    @Positive
    @JsonProperty("quantity")
    private Integer quantity;
}
