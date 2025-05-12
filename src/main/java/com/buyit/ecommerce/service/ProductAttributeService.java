package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.productattributes.ProductAttributesRequest;
import com.buyit.ecommerce.dto.response.productAttributes.ProductAttributesResponseDTO;

import java.util.List;

public interface ProductAttributeService {


    void saveProductAttribute(Long productId, List<ProductAttributesRequest> productAttribute);


    List<ProductAttributesResponseDTO> getProductAttributes(Long productId);
}
