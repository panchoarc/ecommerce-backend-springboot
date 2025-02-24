package com.buyit.ecommerce.dto.request.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "title cannot be blank")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "comment cannot be blank")
    @JsonProperty("comment")
    private String comment;

    @Min(value = 1,message = "rating cannot be lower than 1")
    @Max(value = 5,message = "rating cannot be higher than 5")
    @JsonProperty("rating")
    private Long rating;

    @Positive(message = "product_id cannot be negative")
    @JsonProperty("product_id")
    private Long productId;

}
