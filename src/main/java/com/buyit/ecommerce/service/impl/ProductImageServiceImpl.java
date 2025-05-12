package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.product.ImageUploadForm;
import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductImage;
import com.buyit.ecommerce.exception.custom.ResourceIllegalState;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.ProductImagesMapper;
import com.buyit.ecommerce.repository.ProductImageRepository;
import com.buyit.ecommerce.repository.ProductRepository;
import com.buyit.ecommerce.service.FileService;
import com.buyit.ecommerce.service.ProductImageService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {


    private final FileService fileService;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImagesMapper productImagesMapper;


    @Override
    @Transactional
    public ApiResponse<Void> uploadProductImage(Long productId, List<ImageUploadForm> files) throws IOException {
        Product findedProduct = findById(productId);

        // Elimina todas las imágenes actuales del producto
        List<ProductImage> existingImages = productImageRepository.findByProduct_ProductId(productId);
        for (ProductImage image : existingImages) {
            fileService.deleteFile(image.getUrl()); // Elimina las imágenes del storage si es necesario
        }
        productImageRepository.deleteAllByProduct(findedProduct); // Elimina las imágenes de la base de datos

        long newMainCount = files.stream().filter(f -> Boolean.TRUE.equals(f.getIsMain())).count();

        if (newMainCount > 1) {
            throw new ResourceIllegalState("Only one image can be marked as main.");
        }

        boolean uploadingNewMain = newMainCount == 1;

        // Si se está subiendo una nueva imagen principal, eliminamos la anterior
        if (uploadingNewMain) {
            productImageRepository.clearMainImage(productId);
        }

        // Sube las nuevas imágenes
        for (ImageUploadForm fileForm : files) {
            MultipartFile file = fileForm.getImage();
            Boolean isMain = fileForm.getIsMain();
            String fileUrl = fileService.uploadFile(file); // Subir el archivo

            String extension = file.getContentType();

            // Guarda la nueva imagen en la base de datos
            ProductImage productImage = new ProductImage();
            productImage.setProduct(findedProduct);
            productImage.setUrl(fileUrl);
            productImage.setIsMain(isMain);
            productImage.setExtension(extension);
            productImageRepository.save(productImage);
        }

        return ResponseBuilder.success("Images uploaded successfully", null);
    }


    @Override
    public ApiResponse<List<ProductImagesResponse>> getProductImages(Long id) {
        Product product = findById(id);
        List<ProductImage> relatedProductImages = productImageRepository.findAllByProduct(product);
        List<ProductImagesResponse> response = relatedProductImages.stream()
                .map(productImagesMapper::toProductImages).toList();

        return ResponseBuilder.success("Images retrieved successfully", response);
    }

    @Override
    public void deleteProductImage(Long productId, Long imageId) throws IOException {

        findById(productId);

        Optional<ProductImage> existProductImage = productImageRepository.findByIdAndProductId(imageId, productId);

        if (existProductImage.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete image");
        }

        ProductImage productImage = existProductImage.get();

        String[] parts = productImage.getUrl().split("/");

        String key = parts[parts.length - 1];

        fileService.deleteFile(key);
        productImageRepository.delete(productImage);

    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
    }
}
