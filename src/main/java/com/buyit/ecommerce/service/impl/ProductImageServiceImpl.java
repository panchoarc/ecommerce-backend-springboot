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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {


    private static final String UPLOAD_DIR = "uploads";


    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImagesMapper productImagesMapper;


    @Override
    public ApiResponse<Void> uploadProductImage(MultipartFile[] files, Long productId) throws IOException {

        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ResourceNotFoundException("Product Not Found");
        }

        // Crear el directorio 'uploads' si no existe
        Path uploadDirPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath); // Crea todos los directorios necesarios
        }

        for (MultipartFile file : files) {
            String imageName = generateUniqueImageName(productId, Objects.requireNonNull(file.getOriginalFilename()));
            String extension = file.getContentType();
            Path imagePath = uploadDirPath.resolve(imageName);

            // Guardar el archivo en el directorio
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

            // Guardar la información de la imagen en la base de datos
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product.get());
            productImage.setUrl(imageName); // Nombre del archivo (puedes personalizarlo)
            productImage.setExtension(extension);
            productImageRepository.save(productImage);
        }

        return ResponseBuilder.success("Images attached successfully", null);
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

        return ResponseBuilder.success("Images attached successfully", response);
    }

    private String generateUniqueImageName(Long productId, String imageName) {
        String extension = imageName.substring(imageName.lastIndexOf("."));
        String uniqueId = UUID.randomUUID().toString();
        return "producto_" + productId + "-" + uniqueId + extension;

    }
}
