package com.buyit.ecommerce.dto.response.categoryAttributes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryAttributeDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String inputType;
    @JsonProperty("required")
    private Boolean required;
    @JsonProperty("options")
    private List<String> options;
}
