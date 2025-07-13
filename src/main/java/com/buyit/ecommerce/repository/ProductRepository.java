package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Product;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    Optional<Product> findByName(String name);

    boolean existsByNameAndProductIdNot(@NotBlank(message = "name cannot be blank") String name, Long id);


    @Query("""
                SELECT p FROM Product p
                JOIN ProductCategory pc ON p = pc.product
                WHERE pc.category.categoryId = :categoryId
                AND p.productId <> :productId
                AND p.price BETWEEN :minPrice AND :maxPrice
                AND pc.isActive = true
                ORDER BY p.price ASC
            """)
    List<Product> findTop10SimilarProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Long productId, Pageable pageable);
}
