package com.buyit.ecommerce.service;


import com.buyit.ecommerce.dto.request.order.ProductOrderRequest;
import com.buyit.ecommerce.dto.request.product.CreateProductRequest;
import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.dto.request.product.UpdateProductRequest;
import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.dto.response.product.ProductCatResponse;
import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.dto.response.product.UpdateProductResponse;
import com.buyit.ecommerce.entity.Product;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Page<ProductResponse> getAllProducts(ProductRequest productRequest, int page, int size);


    ProductCatResponse getProductById(Long id);

    CreateProductResponse createProduct(CreateProductRequest createProductRequest) throws IOException;

    UpdateProductResponse updateProduct(Long id, UpdateProductRequest requestProductDTO);

    void deleteProduct(Long id);

    Product getProduct(Long id);
    void updateStockForOrder(List<ProductOrderRequest> cartItems);

    BigDecimal calculateTotalPrice(List<ProductOrderRequest> cartItems);

}
