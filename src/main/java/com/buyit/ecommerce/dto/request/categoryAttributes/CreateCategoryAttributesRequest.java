package com.buyit.ecommerce.dto.request.categoryAttributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class CreateCategoryAttributesRequest {


    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("required")
    private boolean required;
    @JsonProperty("options")
    private List<String> options;
}
