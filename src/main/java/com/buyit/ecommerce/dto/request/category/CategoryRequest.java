package com.buyit.ecommerce.dto.request.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotNull
    @JsonProperty("name")
    private String categoryName;

    @NotNull
    @JsonProperty("is_active")
    private Boolean isActive;
}
