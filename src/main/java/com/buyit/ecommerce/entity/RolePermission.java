package com.buyit.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id = new RolePermissionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;


    @Column(name = "granted_at")
    private LocalDateTime grantedAt = LocalDateTime.now();

    @Column(name = "granted_by")
    private String grantedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
