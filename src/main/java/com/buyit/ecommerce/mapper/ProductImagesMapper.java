package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImagesMapper {


    ProductImagesResponse toProductImages(ProductImage image);
}
