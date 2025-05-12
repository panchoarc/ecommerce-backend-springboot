package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.productattributes.ProductAttributesRequest;
import com.buyit.ecommerce.dto.response.productAttributes.ProductAttributesResponseDTO;
import com.buyit.ecommerce.entity.CategoryAttribute;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductAttribute;
import com.buyit.ecommerce.repository.ProductAttributesRepository;
import com.buyit.ecommerce.service.CategoryAttributeService;
import com.buyit.ecommerce.service.ProductAttributeService;
import com.buyit.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAttributeServiceImpl implements ProductAttributeService {


    private final ProductService productService;
    private final CategoryAttributeService categoryAttributeService;
    private final ProductAttributesRepository productAttributesRepository;


    @Override
    @Transactional
    public void saveProductAttribute(Long productId, List<ProductAttributesRequest> productAttributesRequest) {
        Product product = productService.getProduct(productId);

        // 1. Obtener los atributos actuales del producto
        List<ProductAttribute> existingAttributes = productAttributesRepository.findByProduct(product);

        // 2. Crear sets para facilitar comparación
        Set<String> incomingKeys = new HashSet<>();
        Map<String, ProductAttribute> existingMap = new HashMap<>();

        for (ProductAttribute existing : existingAttributes) {
            String key = existing.getAttribute().getId() + "::" + existing.getValue();
            existingMap.put(key, existing);
        }

        // 3. Insertar nuevos y mantener existentes
        for (ProductAttributesRequest attrRequest : productAttributesRequest) {
            CategoryAttribute categoryAttribute = categoryAttributeService.getCategoryAttributeById(attrRequest.getAttributeId());

            if (attrRequest.getValues() != null) {
                for (String value : attrRequest.getValues()) {
                    String key = categoryAttribute.getId() + "::" + value;
                    incomingKeys.add(key);

                    if (!existingMap.containsKey(key)) {
                        // Nuevo atributo, insertarlo
                        ProductAttribute newAttr = new ProductAttribute();
                        newAttr.setProduct(product);
                        newAttr.setAttribute(categoryAttribute);
                        newAttr.setValue(value);
                        productAttributesRepository.save(newAttr);
                    }
                    // Si ya existe, no hacemos nada
                }
            }
        }

        // 4. Eliminar los que ya no están en la nueva lista
        for (Map.Entry<String, ProductAttribute> entry : existingMap.entrySet()) {
            if (!incomingKeys.contains(entry.getKey())) {
                productAttributesRepository.delete(entry.getValue());
            }
        }
    }

    @Override
    @Transactional
    public List<ProductAttributesResponseDTO> getProductAttributes(Long productId) {

        Product product = productService.getProduct(productId);

        List<ProductAttribute> productAttributes = productAttributesRepository.findByProduct(product);

        // Agrupar por attribute.id y mapear los valores
        Map<Long, List<String>> groupedAttributes = productAttributes.stream()
                .collect(Collectors.groupingBy(
                        attr -> attr.getAttribute().getId(),
                        Collectors.mapping(ProductAttribute::getValue, Collectors.toList())
                ));


        log.info("Grouped product attributes: {}", groupedAttributes);

        List<ProductAttributesResponseDTO> result = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : groupedAttributes.entrySet()) {
            ProductAttributesResponseDTO productAttributesResponseDTO = new ProductAttributesResponseDTO(entry.getKey(), entry.getValue());
            result.add(productAttributesResponseDTO);
        }

        return result;

    }
}
