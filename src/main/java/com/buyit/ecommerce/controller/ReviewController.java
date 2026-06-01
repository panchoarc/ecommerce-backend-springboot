package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.anotations.RequirePermission;
import com.buyit.ecommerce.constants.PermissionsConstants;
import com.buyit.ecommerce.dto.request.review.CreateReviewRequest;
import com.buyit.ecommerce.dto.response.review.CreateReviewResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.service.ReviewService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @RequirePermission(value = PermissionsConstants.REVIEWS_CREATE)
    @PreAuthorize("hasAuthority('"+PermissionsConstants.REVIEWS_CREATE +"')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAPI<CreateReviewResponse> addReview(@AuthenticationPrincipal Jwt user, @Valid @RequestBody CreateReviewRequest reviewRequestDTO) {

        String keycloakId = userService.extractKeycloakIdFromUser(user);
        CreateReviewResponse review = reviewService.createReview(keycloakId, reviewRequestDTO);
        return ResponseBuilder.success("Review added successfully", review);
    }

    @RequirePermission(value = PermissionsConstants.REVIEWS_DELETE)
    @PreAuthorize("hasAuthority('"+PermissionsConstants.REVIEWS_DELETE +"')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseAPI<Void> deleteMyReview(@AuthenticationPrincipal Jwt user, @PathVariable Long id) {
        String keycloakId = userService.extractKeycloakIdFromUser(user);
        reviewService.deleteMyReview(id, keycloakId);

        return ResponseBuilder.success("Review Deleted successfully", null);
    }

    @Public
    @RequirePermission(value = PermissionsConstants.REVIEWS_GET_REVIEW)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.REVIEWS_GET_REVIEW + "')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<ReviewResponse> getMyReview(@AuthenticationPrincipal Jwt user, @PathVariable Long id) {
        String keycloakId = userService.extractKeycloakIdFromUser(user);
        ReviewResponse myReview = reviewService.getMyReview(id, keycloakId);

        return ResponseBuilder.success("Review Found", myReview);
    }


}
