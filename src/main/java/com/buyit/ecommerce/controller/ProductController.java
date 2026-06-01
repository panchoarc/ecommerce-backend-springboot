package com.buyit.ecommerce.controller;

import com.buyit.ecommerce.anotations.Public;
import com.buyit.ecommerce.anotations.RequirePermission;
import com.buyit.ecommerce.constants.PermissionsConstants;
import com.buyit.ecommerce.dto.request.product.CreateProductRequest;
import com.buyit.ecommerce.dto.request.product.ImageUploadWrapper;
import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.dto.request.product.UpdateProductRequest;
import com.buyit.ecommerce.dto.request.productattributes.ProductAttributesRequest;
import com.buyit.ecommerce.dto.response.product.*;
import com.buyit.ecommerce.dto.response.productAttributes.ProductAttributesResponseDTO;
import com.buyit.ecommerce.dto.response.review.ReviewResponse;
import com.buyit.ecommerce.service.ProductAttributeService;
import com.buyit.ecommerce.service.ProductImageService;
import com.buyit.ecommerce.service.ProductService;
import com.buyit.ecommerce.service.ReviewService;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseAPI;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final ProductImageService productImageService;

    private final ProductAttributeService productAttributeService;


    @Public
    @PostMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<List<ProductResponse>> getAllProducts(@Valid @RequestBody(required = false) ProductRequest productRequest,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {

        Page<ProductResponse> allProducts = productService.getAllProducts(productRequest, page, size);
        Pagination pagination = ResponseBuilder.buildPagination(allProducts);
        return ResponseBuilder.successPaginated("Products Found", allProducts.getContent(), pagination);
    }


    @Public
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<ProductCatResponse> getProduct(@PathVariable("id") Long id) {
        ProductCatResponse productById = productService.getProductById(id);
        return ResponseBuilder.success("Product found", productById);
    }

    @RequirePermission(value = PermissionsConstants.PRODUCTS_CREATE)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_CREATE + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAPI<CreateProductResponse> createNewProduct(@Valid @RequestBody CreateProductRequest productJson) throws IOException {
        CreateProductResponse product = productService.createProduct(productJson);
        return ResponseBuilder.success("Product Created Successfully", product);
    }

    @RequirePermission(value = PermissionsConstants.PRODUCTS_UPDATE)

    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_UPDATE + "')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<UpdateProductResponse> updateProduct(@PathVariable("id") Long id,
                                                            @Valid @RequestBody UpdateProductRequest requestProductDTO) {
        UpdateProductResponse response = productService.updateProduct(id, requestProductDTO);
        return ResponseBuilder.success("Product Updated Successfully", response);
    }

    @RequirePermission(value = PermissionsConstants.PRODUCTS_DELETE)

    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_DELETE + "')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }


    @RequirePermission(value = PermissionsConstants.PRODUCTS_ATTACH_IMAGES)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_ATTACH_IMAGES + "')")
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAPI<Void> addImagesToProduct(@PathVariable("id") Long id,
                                                @ModelAttribute ImageUploadWrapper imgWrapper) throws IOException {

        return productImageService.uploadProductImage(id, imgWrapper.getImages());
    }


    @RequirePermission(value = PermissionsConstants.PRODUCTS_GET_PRODUCT_IMAGES)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_GET_PRODUCT_IMAGES + "')")
    @Public
    @GetMapping(value = "/{id}/images")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<List<ProductImagesResponse>> getProductImages(@PathVariable("id") Long id) {
        return productImageService.getProductImages(id);
    }

    @RequirePermission(value = PermissionsConstants.PRODUCTS_DELETE_PRODUCT_IMAGE)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_DELETE_PRODUCT_IMAGE + "')")
    @DeleteMapping(value = "/{id}/images/{imgId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long productId, @PathVariable("imgId") Long idImage) throws IOException {
        productImageService.deleteProductImage(productId, idImage);
    }

    @RequirePermission(value = PermissionsConstants.PRODUCTS_GET_PRODUCT_REVIEWS)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_GET_PRODUCT_REVIEWS + "')")
    @Public
    @GetMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<List<ReviewResponse>> getProductReviews(@PathVariable("id") Long id) {
        List<ReviewResponse> productReviews = reviewService.getProductReviews(id);
        return ResponseBuilder.success("Reviews found", productReviews);
    }


    @RequirePermission(value = PermissionsConstants.PRODUCTS_ADD_PRODUCT_ATTRIBUTES)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_ADD_PRODUCT_ATTRIBUTES + "')")
    @PostMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseAPI<Void> addAttributesToProduct(@PathVariable("id") Long productId, @Valid @RequestBody List<ProductAttributesRequest> attributes) {

        productAttributeService.saveProductAttribute(productId, attributes);
        return ResponseBuilder.success("Attributes added successfully", null);

    }

    @RequirePermission(value = PermissionsConstants.PRODUCTS_GET_PRODUCT_ATTRIBUTES)
    @PreAuthorize("hasAuthority('" + PermissionsConstants.PRODUCTS_GET_PRODUCT_ATTRIBUTES + "')")
    @GetMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseAPI<List<ProductAttributesResponseDTO>> getProductAttributes(@PathVariable("id") Long productId) {

        List<ProductAttributesResponseDTO> productAttributes = productAttributeService.getProductAttributes(productId);
        return ResponseBuilder.success("Attributes added successfully", productAttributes);

    }
}
