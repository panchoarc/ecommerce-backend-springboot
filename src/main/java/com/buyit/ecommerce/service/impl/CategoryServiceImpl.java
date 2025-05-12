package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.category.CreateCategoryRequest;
import com.buyit.ecommerce.dto.request.category.UpdateCategoryRequest;
import com.buyit.ecommerce.dto.response.category.CategoryMenuResponse;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.dto.response.category.UpdateCategoryResponse;
import com.buyit.ecommerce.entity.Category;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceIllegalState;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.CategoryMapper;
import com.buyit.ecommerce.repository.CategoryRepository;
import com.buyit.ecommerce.repository.specification.CategorySpecification;
import com.buyit.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryMenuResponse> getCategories() {
        List<Category> categoryPage = categoryRepository.findActiveCategories();
        return categoryPage.stream().map(categoryMapper::toCategoryMenuResponse).toList();
    }

    @Override
    public Page<CategoryResponse> getAllCategories(CategoryRequest categoryRequest, int page, int size) {
        Specification<Category> spec = CategorySpecification.getCategorySpecification(categoryRequest);
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

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category found with ID: " + id));
    }
}