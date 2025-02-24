package com.buyit.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "base_path", nullable = false)
    private String basePath;

    @Column(name = "dynamic_path")
    private String dynamicPath;

    @Column(name = "http_method", nullable = false) // Agregamos el campo http_method
    private String httpMethod; // Campo para el método HTTP

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoleEndpoint> roleEndpoint;
}
