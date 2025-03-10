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
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
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
    public ApiResponse<Void> uploadProductImage(MultipartFile[] files, Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ResourceNotFoundException("Product Not Found");
        }

        ensureBucketExists();

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
                productImage.setProduct(product.get());
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
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResourceNotFoundException("Product Not Found");
        }
        List<ProductImage> relatedProductImages = productImageRepository.findAllByProduct(product.get());
        List<ProductImagesResponse> response = relatedProductImages.stream()
                .map(productImagesMapper::toProductImages).toList();

        return ResponseBuilder.success("Images retrieved successfully", response);
    }

    private void ensureBucketExists() {
        try {
            s3Client.headBucket(request -> request.bucket(bucketName));
        } catch (Exception e) {
            log.info("Exception while checking if bucket exists: {}", e.getMessage());
            log.info("Bucket does not exist, creating: {}", bucketName);
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }
    }

    private String generateUniqueImageName(Long productId, String imageName) {
        String extension = imageName.contains(".") ? imageName.substring(imageName.lastIndexOf(".")) : "";
        String uniqueId = UUID.randomUUID().toString();
        return "producto_" + productId + "-" + uniqueId + extension;
    }
}
