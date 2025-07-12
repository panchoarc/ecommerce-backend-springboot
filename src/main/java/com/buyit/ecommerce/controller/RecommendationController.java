package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.service.RecommendationService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {


    private final RecommendationService recommendationService;


    @Public
    @GetMapping()
    public ApiResponse<List<ProductResponse>> getRecommendations() {

        List<ProductResponse> popularProducts = recommendationService.getPopularProducts();
        return ResponseBuilder.success("Recommendations retrieved successfully", popularProducts);
    }

    @Public
    @GetMapping("/{productId}/similar")
    public ApiResponse<List<ProductResponse>> getSimilarProducts(@PathVariable Long productId) {
        List<ProductResponse> similarProducts = recommendationService.getSimilarProducts(productId);
        return ResponseBuilder.success("Recommendations retrieved successfully", similarProducts);
    }

}
