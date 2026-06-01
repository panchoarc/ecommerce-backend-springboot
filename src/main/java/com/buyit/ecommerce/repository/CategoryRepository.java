package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.dto.response.category.CategoryMenuResponse;
import com.buyit.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query("SELECT new com.buyit.ecommerce.dto.response.category.CategoryMenuResponse(c.categoryId, c.name) " +
            "FROM Category c WHERE c.isActive = true")
    List<CategoryMenuResponse> findActiveCategoryMenu();
    Optional<Category> findByName(String name);
}
