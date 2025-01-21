package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.category.CreateCategoryRequest;
import com.buyit.ecommerce.dto.request.category.UpdateCategoryRequest;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.dto.response.category.UpdateCategoryResponse;
import com.buyit.ecommerce.entity.Category;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceIllegalState;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.CategoryMapper;
import com.buyit.ecommerce.repository.CategoryRepository;
import com.buyit.ecommerce.service.CategoryService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponse> getAllCategories(CategoryRequest categoryRequest, int page, int size) {
        Specification<Category> spec = getCategorySpecification(categoryRequest);
        Sort sort = Sort.by(Sort.Direction.ASC, "categoryId");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        return categoryPage.map(categoryMapper::toCategoryResponseDTO);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {

        Category category = getCategory(id);
        return categoryMapper.toCategoryResponseDTO(category);
    }

    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
        Optional<Category> foundCategory = categoryRepository.findByName(createCategoryRequest.getCategoryName());
        if (foundCategory.isPresent()) {
            throw new ResourceExistException("Category already exists");
        }
        Category category = new Category();
        category.setName(createCategoryRequest.getCategoryName());
        category.setDescription(createCategoryRequest.getDescription());
        category.setIsActive(true);

        Category createdCategory = categoryRepository.saveAndFlush(category);

        return categoryMapper.toCategoryCreateResponseDTO(createdCategory);
    }

    @Override
    public UpdateCategoryResponse updateCategory(Long id, UpdateCategoryRequest categoryDetails) {

        Category category = getCategory(id);

        category.setName(categoryDetails.getCategoryName());
        category.setDescription(categoryDetails.getDescription());
        category.setIsActive(categoryDetails.getIsActive());
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryUpdateResponseDTO(updatedCategory);

    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategory(id);

        if (!Boolean.TRUE.equals(category.getIsActive())) {
            throw new ResourceIllegalState("Category is already inactive.");
        }
        category.setIsActive(false);
        categoryRepository.save(category);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category found with ID: " + id));
    }

    public Specification<Category> getCategorySpecification(CategoryRequest categoryRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Agregar predicado para nombre de categoría
            addLikePredicateIfNotEmpty(predicates, criteriaBuilder, root.get("name"), categoryRequest.getCategoryName());

            // Agregar predicado para isActive
            addEqualPredicateIfNotNull(predicates, criteriaBuilder, root.get("isActive"), categoryRequest.getIsActive());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addLikePredicateIfNotEmpty(List<Predicate> predicates, CriteriaBuilder cb, Path<String> path, String value) {
        if (value != null && !value.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(path), "%" + value.trim().toLowerCase() + "%"));
        }
    }

    private void addEqualPredicateIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<?> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }
}
