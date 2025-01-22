package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.category.CreateCategoryRequest;
import com.buyit.ecommerce.dto.request.category.UpdateCategoryRequest;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.dto.response.category.UpdateCategoryResponse;
import org.springframework.data.domain.Page;


public interface CategoryService {

    Page<CategoryResponse> getAllCategories(CategoryRequest categoryRequest, int page, int size);

    CategoryResponse getCategoryById(Long id);

    CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    UpdateCategoryResponse updateCategory(Long id, UpdateCategoryRequest categoryDetails);

    void deleteCategory(Long id);


}