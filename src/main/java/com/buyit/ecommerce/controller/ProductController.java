package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.ValidImage;
import com.buyit.ecommerce.dto.request.product.CreateProductRequest;
import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.dto.request.product.UpdateProductRequest;
import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.dto.response.product.ProductImagesResponse;
import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.dto.response.product.UpdateProductResponse;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.service.ProductImageService;
import com.buyit.ecommerce.service.ProductService;
import com.buyit.ecommerce.service.ReviewService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final ProductImageService productImageService;

    @PostMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ProductResponse>> getAllProducts(@Valid @RequestBody ProductRequest productRequest,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {

        Page<ProductResponse> allProducts = productService.getAllProducts(productRequest, page, size);
        Pagination pagination = ResponseBuilder.buildPagination(allProducts);
        return ResponseBuilder.successPaginated("Products Found", allProducts.getContent(), pagination);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductResponse> getProduct(@PathVariable("id") Long id) {
        ProductResponse productById = productService.getProductById(id);
        return ResponseBuilder.success("Product found", productById);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateProductResponse> createNewProduct(@Valid @RequestBody CreateProductRequest createProductRequest) throws IOException {
        CreateProductResponse product = productService.createProduct(createProductRequest);
        return ResponseBuilder.success("Product Created Successfully", product);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UpdateProductResponse> updateProduct(@PathVariable("id") Long id,
                                                            @Valid @RequestBody UpdateProductRequest requestProductDTO) {
        UpdateProductResponse response = productService.updateProduct(id, requestProductDTO);
        return ResponseBuilder.success("Product Updated Successfully", response);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }


    @PostMapping(value = "/{id}/images")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> addImagesToProduct(@PathVariable("id") Long id,
                                                @Valid @ValidImage @RequestPart("images") List<MultipartFile> images) throws IOException {
        return productImageService.uploadProductImage(images, id);
    }

    @GetMapping(value = "/{id}/images")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ProductImagesResponse>> getProductImages(@PathVariable("id") Long id) {
        return productImageService.getProductImages(id);
    }

    @DeleteMapping(value = "/{id}/images/{imgId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long productId ,@PathVariable("imgId") Long idImage) {
        productImageService.deleteProductImage(productId,idImage);
    }


    @GetMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ReviewResponse>> getProductReviews(@PathVariable("id") Long id) {
        List<ReviewResponse> productReviews = reviewService.getProductReviews(id);
        return ResponseBuilder.success("Reviews found", productReviews);
    }
}
