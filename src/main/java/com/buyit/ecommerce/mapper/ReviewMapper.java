package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.review.CreateReviewResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.dto.response.user.UserReviewResponse;
import com.buyit.ecommerce.entity.Review;
import com.buyit.ecommerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "reviewId", target = "id")
    CreateReviewResponse toCreateReviewDTO(Review savedReview);

    @Mapping(source = "reviewId", target = "id")
    @Mapping(source = "user", target = "user")
    ReviewResponse toReviewDTO(Review review);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(expression = "java(user.getFirstName() + \" \" + user.getLastName())", target = "fullName")
    UserReviewResponse toUserReviewDTO(User user);
}
