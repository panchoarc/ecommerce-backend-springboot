package com.buyit.ecommerce.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}
