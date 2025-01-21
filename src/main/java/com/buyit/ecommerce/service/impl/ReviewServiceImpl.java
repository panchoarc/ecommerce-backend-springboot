package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.review.CreateReviewRequest;
import com.buyit.ecommerce.dto.response.review.CreateReviewResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.Review;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.ReviewMapper;
import com.buyit.ecommerce.repository.ReviewRepository;
import com.buyit.ecommerce.service.ProductService;
import com.buyit.ecommerce.service.ReviewService;
import com.buyit.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final ReviewMapper reviewMapper;
    private final UserService userService;

    @Override
    public CreateReviewResponse createReview(String keycloakUserId, CreateReviewRequest reviewRequestDTO) {

        User dbUser = userService.getUserByKeycloakId(keycloakUserId);
        Product requestedProduct = productService.getProduct(reviewRequestDTO.getProductId());

        Review review = new Review();
        review.setUser(dbUser);
        review.setComment(reviewRequestDTO.getComment());
        review.setTitle(reviewRequestDTO.getTitle());
        review.setProduct(requestedProduct);
        review.setRating(reviewRequestDTO.getRating());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toCreateReviewDTO(savedReview);
    }

    @Override
    public List<ReviewResponse> getProductReviews(Long id) {

        Product dbProduct = productService.getProduct(id);
        List<Review> reviews = reviewRepository.findByProduct(dbProduct);
        List<ReviewResponse> mappedReviews = List.of();
        if (!reviews.isEmpty()) {
            mappedReviews = reviews.stream().map(reviewMapper::toReviewDTO).toList();
        }
        return mappedReviews;

    }

    @Override
    public void deleteMyReview(Long id, String keycloakUserId) {

        User dbUser = userService.getUserByKeycloakId(keycloakUserId);
        Review review = getReview(id);

        verifyOwnership(review, dbUser);
        reviewRepository.deleteById(id);
    }

    @Override
    public ReviewResponse getMyReview(Long id, String keycloakUserId) {

        User dbUser = userService.getUserByKeycloakId(keycloakUserId);

        Review review = getReview(id);
        verifyOwnership(review, dbUser);

        return reviewMapper.toReviewDTO(review);
    }

    private static void verifyOwnership(Review review, User dbUser) {
        if (!review.getUser().getUserId().equals(dbUser.getUserId())) {
            throw new DeniedAccessException("You are not allowed to delete this review");
        }
    }

    private Review getReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

    }
}
