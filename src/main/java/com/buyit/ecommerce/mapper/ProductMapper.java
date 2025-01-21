package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.dto.response.product.UpdateProductResponse;
import com.buyit.ecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    ProductResponse toProductResponseDTO(Product product);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    CreateProductResponse toCreateProductResponseDTO(Product product);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    UpdateProductResponse toUpdateProductResponseDTO(Product product);

}
