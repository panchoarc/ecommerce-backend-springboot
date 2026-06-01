package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.repository.projection.ProductRecommendationProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Product,Long> {


    @Query("""
    SELECT
        p.productId AS id,
        p.name AS name,
        p.description AS description,
        p.price AS price,
        p.stockQuantity AS stock,
        (
            SELECT pi.url
            FROM ProductImage pi
            WHERE pi.product.productId = p.productId
            AND pi.isMain = true
        ) AS img,
        COALESCE(AVG(r.rating), 0) AS rating
    FROM Product p
    JOIN OrderItem oi ON oi.product.productId = p.productId
    LEFT JOIN Review r ON r.product.productId = p.productId
    GROUP BY p.productId, p.name, p.description, p.price, p.stockQuantity
    ORDER BY SUM(oi.quantity) DESC
""")
    List<ProductRecommendationProjection> getPopularProducts(Pageable pageable);


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
