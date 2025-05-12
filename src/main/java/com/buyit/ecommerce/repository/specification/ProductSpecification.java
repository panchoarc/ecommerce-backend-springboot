package com.buyit.ecommerce.repository.specification;

import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductAttribute;
import com.buyit.ecommerce.entity.ProductCategory;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

@Slf4j
public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> getProductSpecification(ProductRequest request) {


        return (root, query, cb) -> {
            query.distinct(true); // Asegura resultados únicos
            var predicates = cb.conjunction();

            // Filtros base
            predicates = cb.and(predicates, cb.equal(root.get("isActive"), true));
            predicates = cb.and(predicates, cb.greaterThan(root.get("stockQuantity"), 0));

            if (request == null) return predicates;

            // Filtro por nombre
            if (hasText(request.getName())) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("name")), containsIgnoreCase(request.getName())));
            }

            // Filtro por descripción
            if (hasText(request.getDescription())) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("description")), containsIgnoreCase(request.getDescription())));
            }

            // Filtro por precio mínimo
            if (request.getPrice() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("price"), request.getPrice()));
            }

            // Filtro por categoría
            if (request.getCategoryId() != null) {
                Join<Product, ProductCategory> categoryJoin = root.join("categories", JoinType.INNER);
                predicates = cb.and(predicates,
                        cb.equal(categoryJoin.get("category").get("id"), request.getCategoryId()));
            }

            // Filtro por atributos dinámicos
            if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
                for (Map.Entry<Long, List<String>> entry : request.getAttributes().entrySet()) {
                    Long key = entry.getKey();
                    List<String> values = entry.getValue();

                    log.info("key: {}, values: {}", key, values);

                    if (hasValues(values)) {
                        Join<Product, ProductAttribute> attrJoin = root.join("attributeValues", JoinType.INNER);
                        predicates = cb.and(predicates,
                                cb.equal(attrJoin.get("attribute").get("id"), key),
                                attrJoin.get("value").in(values));
                    }
                }
            }

            return predicates;
        };
    }

    private static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private static boolean hasValues(List<String> list) {
        return list != null && !list.isEmpty();
    }

    private static String containsIgnoreCase(String input) {
        return "%" + input.toLowerCase() + "%";
    }
}
