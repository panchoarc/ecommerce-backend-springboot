package com.buyit.ecommerce.dto.request.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

    @NotBlank(message = "name cannot be blank")
    @JsonProperty("name")
    private String categoryName;

    @NotEmpty(message = "description cannot be blank")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "is_active needs to be a boolean value")
    @JsonProperty("is_active")
    private Boolean isActive;

}
