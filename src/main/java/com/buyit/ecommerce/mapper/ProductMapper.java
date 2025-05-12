package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.dto.response.product.ProductCatResponse;
import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.dto.response.product.UpdateProductResponse;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductCategory;
import com.buyit.ecommerce.entity.ProductImage;
import com.buyit.ecommerce.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    @Mapping(target = "img", expression = "java(getMainImageUrl(product))")
    @Mapping(target = "rating", expression = "java(calculateAverageRating(product))")
    ProductResponse toProductResponseDTO(Product product);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    @Mapping(target = "img", expression = "java(getMainImageUrl(product))")
    @Mapping(target = "rating", expression = "java(calculateAverageRating(product))")
    @Mapping(target = "categoryId", expression = "java(getFirstCategoryId(product))")
    @Mapping(source = "isActive",target = "isActive")
    ProductCatResponse toProductCatResponseDTO(Product product);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    @Mapping(target = "img", expression = "java(getMainImageUrl(product))")
    CreateProductResponse toCreateProductResponseDTO(Product product);

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "stockQuantity", target = "stock")
    @Mapping(target = "img", expression = "java(getMainImageUrl(product))")
    UpdateProductResponse toUpdateProductResponseDTO(Product product);


    default String getMainImageUrl(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null; // Si no hay imágenes, retorna null
        }

        return product.getImages().stream()
                .filter(ProductImage::getIsMain)  // Filtra las imágenes principales
                .map(ProductImage::getUrl)        // Obtiene la URL de la imagen principal
                .findFirst()                      // Toma la primera (si existe)
                .orElse(null);                    // Si no hay imagen principal, retorna null
    }

    default Double calculateAverageRating(Product product) {
        if (product.getReviews() == null || product.getReviews().isEmpty()) {
            return 0.0;
        }
        return product.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    default Long getFirstCategoryId(Product product) {
        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
            ProductCategory firstCategory = product.getCategories().get(0);
            if (firstCategory.getCategory() != null) {
                return firstCategory.getCategory().getCategoryId();
            }
        }
        return null;
    }
}
