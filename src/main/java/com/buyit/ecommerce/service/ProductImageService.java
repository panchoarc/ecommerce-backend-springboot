package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.request.product.ImageUploadForm;
import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.util.ApiResponse;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {

    ApiResponse<Void> uploadProductImage(Long productId, List<ImageUploadForm> files) throws IOException;

    ApiResponse<List<ProductImagesResponse>> getProductImages(Long id);

    void deleteProductImage(Long productId, Long imageId) throws IOException;

}
