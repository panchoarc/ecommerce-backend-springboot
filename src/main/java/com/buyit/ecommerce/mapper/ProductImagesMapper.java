package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImagesMapper {


    @Mapping(source = "isMain",target = "isMainImage")
    ProductImagesResponse toProductImages(ProductImage image);
}
