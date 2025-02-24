package com.buyit.ecommerce.dto.request.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {


    @JsonProperty("name")
    private String categoryName;

    @JsonProperty("is_active")
    private Boolean isActive;
}
