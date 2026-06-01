package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByCode(String code);

    @Query("""
            SELECT COUNT(rp) > 0
            FROM User u
            JOIN u.roles r
            JOIN r.rolePermissions rp
            JOIN rp.permission p
            WHERE u.keycloakUserId = :userId
            AND p.code = :permission
            """)
    boolean hasPermission(String userId, String permission);

    @Query("""
    SELECT p FROM Permission p
    WHERE p.id NOT IN (
        SELECT rp.permission.id FROM RolePermission rp
        WHERE rp.role.id = :roleId AND rp.isActive = true
    )
""")
    List<Permission> findMissingPermissions(Long roleId);
}
