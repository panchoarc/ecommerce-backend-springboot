package com.buyit.ecommerce.repository.specification;

import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> getProductSpecification(ProductRequest productRequest) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            String name = productRequest.getName();

            if (name != null && !name.isEmpty()) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%")
                );
            }
            String description = productRequest.getDescription();

            if (description != null && !description.isEmpty()) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%")
                );
            }

            BigDecimal minPrice = productRequest.getPrice();

            if (minPrice != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)
                );
            }

            return predicates;
        };
    }
}
