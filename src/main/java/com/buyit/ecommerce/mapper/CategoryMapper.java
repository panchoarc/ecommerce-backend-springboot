package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.category.CategoryResponse;
import com.buyit.ecommerce.dto.response.category.CreateCategoryResponse;
import com.buyit.ecommerce.dto.response.category.UpdateCategoryResponse;
import com.buyit.ecommerce.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "categoryId", target = "id")
    CategoryResponse toCategoryResponseDTO(Category category);

    @Mapping(source = "categoryId", target = "id")
    CreateCategoryResponse toCategoryCreateResponseDTO(Category category);

    @Mapping(source = "categoryId", target = "id")
    @Mapping(source = "isActive",target = "isActive")
    UpdateCategoryResponse toCategoryUpdateResponseDTO(Category updatedCategory);

}
