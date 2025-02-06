package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

    Optional<Endpoint> findByUrlAndHttpMethod(String pattern, String name);

    Optional<Endpoint> findByBasePathAndHttpMethod(String basePath, String name);
}
