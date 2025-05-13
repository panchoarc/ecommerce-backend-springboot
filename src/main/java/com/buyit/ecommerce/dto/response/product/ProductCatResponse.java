package com.buyit.ecommerce.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCatResponse {


    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private BigDecimal price;
    @JsonProperty("stock")
    private Integer stock;
    @JsonProperty("img")
    private String img;
    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("category_id")
    private Long categoryId;
}
