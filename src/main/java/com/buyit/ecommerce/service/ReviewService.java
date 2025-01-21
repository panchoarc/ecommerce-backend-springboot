package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.review.CreateReviewRequest;
import com.buyit.ecommerce.dto.response.review.CreateReviewResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ReviewService {


    CreateReviewResponse createReview(String keycloakUserId, @Valid CreateReviewRequest reviewRequestDTO);

    List<ReviewResponse> getProductReviews(Long id);

    void deleteMyReview(Long id, String keycloakUserId);

    ReviewResponse getMyReview(Long id, String keycloakUserId);

}
