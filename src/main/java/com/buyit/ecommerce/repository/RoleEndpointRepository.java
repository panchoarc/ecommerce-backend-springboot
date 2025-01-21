package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Role;
import com.buyit.ecommerce.entity.RoleEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleEndpointRepository extends JpaRepository<RoleEndpoint, Long> {

    List<RoleEndpoint> findByRole(Role role);

    @Query("SELECT re.role.name FROM role_endpoint re WHERE re.endpoint.id = :endpointId")
    List<String> findRoleIdsByEndpoint(Long endpointId);
}
