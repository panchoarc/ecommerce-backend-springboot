package com.buyit.ecommerce.mapper;


import com.buyit.ecommerce.entity.ProductCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategory productCategoryToProductCategory(ProductCategory productCategory);
}
