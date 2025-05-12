package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.category.CreateCategoryRequest;
import com.buyit.ecommerce.dto.request.category.UpdateCategoryRequest;
import com.buyit.ecommerce.dto.response.category.CategoryMenuResponse;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.dto.response.category.UpdateCategoryResponse;
import com.buyit.ecommerce.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;


public interface CategoryService {


    List<CategoryMenuResponse> getCategories();

    Page<CategoryResponse> getAllCategories(CategoryRequest categoryRequest, int page, int size);

    CategoryResponse getCategoryById(Long id);

    CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

    UpdateCategoryResponse updateCategory(Long id, UpdateCategoryRequest categoryDetails);

    void deleteCategory(Long id);

    Category getCategory(Long id);

}