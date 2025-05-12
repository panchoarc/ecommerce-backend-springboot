package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.categoryAttributes.CreateCategoryAttributesRequest;
import com.buyit.ecommerce.dto.response.categoryAttributes.CategoryAttributeDTO;
import com.buyit.ecommerce.entity.CategoryAttribute;

import java.util.List;

public interface CategoryAttributeService {


    CategoryAttribute getCategoryAttributeById(Long id);
    void createCategoryAttributes(Long categoryId, List<CreateCategoryAttributesRequest> categoryAttribute);

    List<CategoryAttributeDTO> getCategoryAttributes(Long categoryId);


    void updateCategoryAttributes(Long categoryId, List<CreateCategoryAttributesRequest> categoryAttribute);
}
