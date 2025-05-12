package com.buyit.ecommerce.repository;


import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributesRepository extends JpaRepository<ProductAttribute, Long> {
    List<ProductAttribute> findByProduct(Product product);
}
