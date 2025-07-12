package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductCategory;
import com.buyit.ecommerce.mapper.ProductMapper;
import com.buyit.ecommerce.repository.ProductRepository;
import com.buyit.ecommerce.repository.RecommendationRepository;
import com.buyit.ecommerce.service.RecommendationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {


    private final RecommendationRepository recommendationRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public List<ProductResponse> getPopularProducts() {

        List<Product> popularProducts = recommendationRepository.getPopularProducts();

        return popularProducts.stream().map(productMapper::toProductResponseDTO).toList();

    }

    @Override
    public List<ProductResponse> getPopularProductsByCategory(Long categoryId) {
        return List.of();
    }

    @Override
    @Transactional
    public List<ProductResponse> getSimilarProducts(Long productId) {
        Product base = productRepository.findById(productId).orElseThrow();
        Long categoryId = base.getCategories()
                .stream()
                .filter(ProductCategory::getIsActive)
                .findFirst()
                .map(pc -> pc.getCategory().getCategoryId())
                .orElseThrow();
        BigDecimal minPrice = base.getPrice().multiply(BigDecimal.valueOf(0.8));
        BigDecimal maxPrice = base.getPrice().multiply(BigDecimal.valueOf(1.2));

        List<Product> top10Products = productRepository.findTop10SimilarProducts(
                categoryId, minPrice, maxPrice, productId, PageRequest.of(0, 10)
        );

        return top10Products.stream().map(productMapper::toProductResponseDTO).toList();

    }

    @Override
    public List<ProductResponse> getFrequentlyBoughtTogether(Long productId) {
        return List.of();
    }
}
