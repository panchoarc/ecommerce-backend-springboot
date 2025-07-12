package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.response.product.ProductResponse;

import java.util.List;

public interface RecommendationService {


    List<ProductResponse> getPopularProducts();

    List<ProductResponse> getSimilarProducts(Long productId);

    List<ProductResponse> getPopularProductsByCategory(Long categoryId);

    List<ProductResponse> getFrequentlyBoughtTogether(Long productId);
}
