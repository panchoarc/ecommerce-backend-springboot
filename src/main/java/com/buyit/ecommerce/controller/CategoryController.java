package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.dto.request.category.CategoryRequest;
import com.buyit.ecommerce.dto.request.category.CreateCategoryRequest;
import com.buyit.ecommerce.dto.request.category.UpdateCategoryRequest;
import com.buyit.ecommerce.dto.request.categoryAttributes.CreateCategoryAttributesRequest;
import com.buyit.ecommerce.dto.response.category.CategoryMenuResponse;
import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.dto.response.category.UpdateCategoryResponse;
import com.buyit.ecommerce.dto.response.categoryAttributes.CategoryAttributeDTO;
import com.buyit.ecommerce.service.CategoryAttributeService;
import com.buyit.ecommerce.service.CategoryService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryAttributeService categoryAttributeService;


    @Public
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<CategoryMenuResponse>> getCategories() {
        List<CategoryMenuResponse> categories = categoryService.getCategories();
        return ResponseBuilder.success("Categories fetched successfully", categories);
    }


    @Public
    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<CategoryResponse>> getAllCategories(@Valid @RequestBody(required = false) CategoryRequest categoryRequest,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {

        Page<CategoryResponse> allCategories = categoryService.getAllCategories(categoryRequest, page, size);
        Pagination pagination = ResponseBuilder.buildPagination(allCategories);
        return ResponseBuilder.successPaginated("Categories Found", allCategories.getContent(), pagination);

    }

    @Public
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse categoryById = categoryService.getCategoryById(id);
        return ResponseBuilder.success("Category Found", categoryById);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryResponse category = categoryService.createCategory(createCategoryRequest);
        return ResponseBuilder.success("Category created successfully", category);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UpdateCategoryResponse> updateCategory(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        UpdateCategoryResponse updateCategory = categoryService.updateCategory(id, updateCategoryRequest);
        return ResponseBuilder.success("Category updated successfully", updateCategory);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseBuilder.success("Category deleted successfully", null);
    }


    @PostMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> createProductAttributes(@PathVariable("id") Long id, @Valid @RequestBody List<CreateCategoryAttributesRequest> attributesRequest) {
        categoryAttributeService.createCategoryAttributes(id, attributesRequest);
        return ResponseBuilder.success("Attributes added", null);
    }

    @Public
    @GetMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<CategoryAttributeDTO>> getProductAttributes(@PathVariable("id") Long id) {
        List<CategoryAttributeDTO> categoryAttributes = categoryAttributeService.getCategoryAttributes(id);
        return ResponseBuilder.success("Attributes retrieved", categoryAttributes);
    }

    @PutMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> getProductAttributes(@PathVariable("id") Long id, @Valid @RequestBody List<CreateCategoryAttributesRequest> attributesRequest) {
        categoryAttributeService.updateCategoryAttributes(id, attributesRequest);
        return ResponseBuilder.success("Attributes updated", null);
    }

}