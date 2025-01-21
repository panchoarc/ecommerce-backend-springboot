package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.dto.request.review.CreateReviewRequest;
import com.buyit.ecommerce.dto.response.review.CreateReviewResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.service.ReviewService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateReviewResponse> addReview(@AuthenticationPrincipal Jwt user, @Valid @RequestBody CreateReviewRequest reviewRequestDTO) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        CreateReviewResponse review = reviewService.createReview(keycloakId, reviewRequestDTO);
        return ResponseBuilder.success("Review added successfully", review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteMyReview(@AuthenticationPrincipal Jwt user, @PathVariable Long id) {
        String keycloakId = userService.extractKeycloakIdFromUser(user);
        reviewService.deleteMyReview(id, keycloakId);

        return ResponseBuilder.success("Review Deleted successfully", null);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReviewResponse> getMyReview(@AuthenticationPrincipal Jwt user, @PathVariable Long id) {
        String keycloakId = userService.extractKeycloakIdFromUser(user);
        ReviewResponse myReview = reviewService.getMyReview(id, keycloakId);

        return ResponseBuilder.success("Review Found", myReview);
    }


}
