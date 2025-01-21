package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.review.CreateReviewResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "reviewId", target = "id")
    CreateReviewResponse toCreateReviewDTO(Review savedReview);

    @Mapping(source = "reviewId", target = "id")
    ReviewResponse toReviewDTO(Review review);
}
