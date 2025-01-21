package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.util.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {

    ApiResponse<Void> uploadProductImage(MultipartFile[] files, Long productId) throws IOException;

    ApiResponse<List<ProductImagesResponse>> getProductImages(Long id);

}
