package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.AttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {
}
