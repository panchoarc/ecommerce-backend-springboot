package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.order.ProductOrderRequest;
import com.buyit.ecommerce.dto.request.product.CreateProductRequest;
import com.buyit.ecommerce.dto.request.product.ProductRequest;
import com.buyit.ecommerce.dto.request.product.UpdateProductRequest;
import com.buyit.ecommerce.dto.response.product.CreateProductResponse;
import com.buyit.ecommerce.dto.response.product.ProductCatResponse;
import com.buyit.ecommerce.dto.response.product.ProductResponse;
import com.buyit.ecommerce.dto.response.product.UpdateProductResponse;
import com.buyit.ecommerce.entity.Category;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductCategory;
import com.buyit.ecommerce.exception.custom.InsufficientQuantityException;
import com.buyit.ecommerce.exception.custom.ResourceExistException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.ProductMapper;
import com.buyit.ecommerce.repository.CategoryRepository;
import com.buyit.ecommerce.repository.ProductCategoryRepository;
import com.buyit.ecommerce.repository.ProductRepository;
import com.buyit.ecommerce.repository.specification.ProductSpecification;
import com.buyit.ecommerce.service.ProductService;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Page<ProductResponse> getAllProducts(ProductRequest productRequest, int page, int size) {

        Specification<Product> spec = ProductSpecification.getProductSpecification(productRequest);
        Sort sort = Sort.by(Sort.Direction.ASC, "productId");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(productMapper::toProductResponseDTO);

    }

    @Override
    @Transactional
    public ProductCatResponse getProductById(Long id) {

        Product product = getProduct(id);
        return productMapper.toProductCatResponseDTO(product);
    }

    @Override
    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest createProductRequest) {

        boolean existingProduct = isExistingProduct(createProductRequest.getName());
        if (existingProduct) {
            throw new ResourceExistException("Product with name " + createProductRequest.getName() + " already exists");
        }

        boolean isCategoriesValid = isCategoriesValid(createProductRequest.getCategoryIds());

        if (!isCategoriesValid) {
            throw new ResourceNotFoundException("Some category IDs are not valid");
        }

        List<Category> categories = getCategories(createProductRequest.getCategoryIds());

        Product newProduct = new Product();
        newProduct.setName(createProductRequest.getName());
        newProduct.setDescription(createProductRequest.getDescription());
        newProduct.setPrice(createProductRequest.getPrice());
        newProduct.setStockQuantity(createProductRequest.getQuantity());
        newProduct.setIsActive(true);

        Product savedProduct = productRepository.save(newProduct);

        updateProductCategories(categories, savedProduct);
        return productMapper.toCreateProductResponseDTO(savedProduct);
    }

    @Override
    public UpdateProductResponse updateProduct(Long id, UpdateProductRequest requestProductDTO) {
        Product existingProduct = getProduct(id); // obtiene el producto actual

        // Verifica si existe otro producto con el mismo nombre (distinto ID)
        boolean nameConflict = productRepository.existsByNameAndProductIdNot(requestProductDTO.getName(), id);
        if (nameConflict) {
            throw new ResourceExistException("Product with name " + requestProductDTO.getName() + " already exists");
        }

        boolean isCategoriesValid = isCategoriesValid(Collections.singletonList(requestProductDTO.getCategoryId()));
        if (!isCategoriesValid) {
            throw new ResourceNotFoundException("Some category IDs are not valid");
        }

        List<Category> categories = getCategories(Collections.singletonList(requestProductDTO.getCategoryId()));

        existingProduct.setName(requestProductDTO.getName());
        existingProduct.setDescription(requestProductDTO.getDescription());
        existingProduct.setPrice(requestProductDTO.getPrice());
        existingProduct.setStockQuantity(requestProductDTO.getQuantity());
        existingProduct.setIsActive(requestProductDTO.getIsActive());

        Product savedProduct = productRepository.save(existingProduct);
        updateProductCategories(categories, savedProduct);

        return productMapper.toUpdateProductResponseDTO(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProduct(id);
        product.setIsActive(false);
        productRepository.save(product);
        ResponseBuilder.success("Product deleted successfully", null);
    }

    @Override
    public void updateStockForOrder(List<ProductOrderRequest> cartItems) {
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        List<Product> productList = new ArrayList<>();

        // 4. Validar productos y stock
        for (ProductOrderRequest cartItem : cartItems) {
            Long productId = cartItem.getProductId();
            Integer requestedQuantity = cartItem.getQuantity();


            // Obtener producto desde la base de datos
            Product product = getProduct(productId);


            // Validar stock disponible
            if (product.getStockQuantity() < requestedQuantity) {
                throw new InsufficientQuantityException("Insufficient stock for product ID " + productId);
            }


            // Actualizar el total de la orden
            totalOrderAmount = totalOrderAmount.add(product.getPrice().multiply(BigDecimal.valueOf(requestedQuantity)));

            // Reducir stock del producto
            product.setStockQuantity(product.getStockQuantity() - requestedQuantity);
            productList.add(product);
        }

        productRepository.saveAll(productList);


    }

    @Override
    public BigDecimal calculateTotalPrice(List<ProductOrderRequest> cartItems) {
        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for (ProductOrderRequest cartItem : cartItems) {
            Long productId = cartItem.getProductId();
            Integer requestedQuantity = cartItem.getQuantity();

            Product product = getProduct(productId);

            // Calcular el monto total de la orden
            totalOrderAmount = totalOrderAmount.add(product.getPrice().multiply(BigDecimal.valueOf(requestedQuantity)));
        }

        return totalOrderAmount;
    }

    public void updateProductCategories(List<Category> categories, Product savedProduct) {
        // Obtener las categorías existentes para el producto desde la base de datos
        List<ProductCategory> existingProductCategories = productCategoryRepository.findByProduct(savedProduct);

        // Crear una lista con las categorías nuevas que se deben agregar
        List<ProductCategory> newProductCategories = categories.stream()
                .filter(category -> existingProductCategories.stream()
                        .noneMatch(productCategory -> productCategory.getCategory().equals(category))) // Filtra las categorías que no están asociadas aún
                .map(category -> {
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setProduct(savedProduct);
                    productCategory.setCategory(category);
                    productCategory.setIsActive(true);
                    return productCategory;
                }).toList();

        // Crear una lista con las categorías que ya no deberían estar asociadas
        List<ProductCategory> categoriesToRemove = existingProductCategories.stream()
                .filter(productCategory -> !categories.contains(productCategory.getCategory()))
                .toList();

        productCategoryRepository.deleteAll(categoriesToRemove);

        productCategoryRepository.saveAll(newProductCategories);
    }

    public boolean isCategoriesValid(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return false;
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        return categoryIds.size() == categories.size() &&
                categories.stream().allMatch(Category::getIsActive);
    }

    public List<Category> getCategories(List<Long> categoryIds) {
        return categoryRepository.findAllById(categoryIds);
    }

    public boolean isExistingProduct(String name) {
        Optional<Product> product = productRepository.findByName(name);
        return product.isPresent();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }


}
