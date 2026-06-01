package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission,Long> {

    @Query("""
    SELECT rp.permission.id
    FROM RolePermission rp
    WHERE rp.role.id = :roleId
""")
    Set<Long> findPermissionIdsByRoleId(Long roleId);
}
