package com.buyit.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "address")
@Getter
@Setter
@ToString(exclude = "user")
public class Address {

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name = "alias")
    private String alias;

    @Column(name = "street")
    private String street;
    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;
    @Column(name = "postal_code")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
