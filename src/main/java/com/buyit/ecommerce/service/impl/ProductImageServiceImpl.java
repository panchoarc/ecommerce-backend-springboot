package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductImage;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.ProductImagesMapper;
import com.buyit.ecommerce.repository.ProductImageRepository;
import com.buyit.ecommerce.repository.ProductRepository;
import com.buyit.ecommerce.service.ProductImageService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImagesMapper productImagesMapper;

    @Override
    public ApiResponse<Void> uploadProductImage(List<MultipartFile> files, Long productId) {
        Product findedProduct = findById(productId);

        for (MultipartFile file : files) {
            String imageName = generateUniqueImageName(productId, Objects.requireNonNull(file.getOriginalFilename()));
            String extension = file.getContentType();

            try {
                byte[] fileBytes = file.getBytes(); // Convertir a bytes correctamente
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imageName)
                        .contentType(file.getContentType())
                        .contentLength((long) fileBytes.length) // Asegurar que el tamaño sea correcto
                        .build();

                s3Client.putObject(request, RequestBody.fromBytes(fileBytes));

                String imageUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(imageName)).toExternalForm();

                ProductImage productImage = new ProductImage();
                productImage.setProduct(findedProduct);
                productImage.setUrl(imageUrl);
                productImage.setExtension(extension);
                productImageRepository.save(productImage);
            } catch (Exception e) {
                log.error("❌ Error uploading image {} to S3: {}", imageName, e.getMessage());
                throw new RuntimeException("Failed to upload image: " + file.getOriginalFilename());
            }
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
    public void deleteProductImage(Long productId, Long imageId) {

        findById(productId);

        Optional<ProductImage> existProductImage = productImageRepository.findByIdAndProductId(imageId, productId);

        if (existProductImage.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete image");
        }

        ProductImage productImage = existProductImage.get();

        String[] parts = productImage.getUrl().split("/");

        String key = parts[parts.length - 1];

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        productImageRepository.delete(productImage);

    }

    private String generateUniqueImageName(Long productId, String imageName) {
        String extension = imageName.contains(".") ? imageName.substring(imageName.lastIndexOf(".")) : "";
        String uniqueId = UUID.randomUUID().toString();
        return "producto_" + productId + "-" + uniqueId + extension;
    }

    public Product findById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
    }
}
