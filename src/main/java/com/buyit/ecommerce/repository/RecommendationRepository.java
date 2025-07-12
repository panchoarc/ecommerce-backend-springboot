package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Product,Long> {


    @Query("""
    SELECT p
    FROM Product p
    WHERE p.productId IN (
        SELECT oi.product.productId
        FROM OrderItem oi
        GROUP BY oi.product.productId
        ORDER BY COUNT(oi.product.productId) DESC
    )
""")
    @EntityGraph(attributePaths = {"images", "categories", "reviews"})
    List<Product> getPopularProducts();


    @Query("""
    SELECT p FROM Product p
    JOIN ProductCategory pc ON p.productId = pc.product.productId
    WHERE pc.category.categoryId = :categoryId
    AND p.price BETWEEN :minPrice AND :maxPrice
    AND p.productId <> :excludeId
    ORDER BY p.createdAt DESC
""")
    List<Product> findTop10ByCategoryAndPriceRangeAndExclude(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("excludeId") Long excludeId
    );

}
