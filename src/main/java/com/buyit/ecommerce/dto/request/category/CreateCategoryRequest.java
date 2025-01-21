package com.buyit.ecommerce.dto.request.category;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {

    @NotEmpty
    @JsonProperty("name")
    private String categoryName;

    @NotEmpty
    @JsonProperty("description")
    private String description;


}
