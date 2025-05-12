package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findAllByProduct(Product product);

    @Query("select pimg from ProductImage pimg where pimg.id=?1 and pimg.product.productId=?2")
    Optional<ProductImage> findByIdAndProductId(Long imgId, Long productId);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isMain = false WHERE pi.product.productId = :productId AND pi.isMain = true")
    void clearMainImage(@Param("productId") Long productId);

    List<ProductImage> findByProduct_ProductId(Long productProductId);

    void deleteAllByProduct(Product product);
}
