package com.buyit.ecommerce.repository.specification;

import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.entity.Category;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {

    private CategorySpecification() {
    }

    public static Specification<Category> getCategorySpecification(CategoryRequest categoryRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Agregar predicado para nombre de categoría
            addLikePredicateIfNotEmpty(predicates, criteriaBuilder, root.get("name"), categoryRequest.getCategoryName());

            // Agregar predicado para isActive
            addEqualPredicateIfNotNull(predicates, criteriaBuilder, root.get("isActive"), categoryRequest.getIsActive());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addLikePredicateIfNotEmpty(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, String value) {
        if (value != null && !value.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(path), "%" + value.trim().toLowerCase() + "%"));
        }
    }

    private static void addEqualPredicateIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<?> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }
}
