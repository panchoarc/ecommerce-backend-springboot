package com.buyit.ecommerce.dto.response.order;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {

    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private UserDTO user;
    private AddressDTO address;
    private List<ItemDTO> items;

    // Clases internas
    @Getter
    @Setter
    public static class UserDTO {
        private String fullName;
        private String email;
        // Getters/setters
    }

    @Getter
    @Setter
    public static class AddressDTO {
        private String street;
        private String city;
        private String country;
        private String postalCode;
        // Getters/setters
    }

    @Getter
    @Setter
    public static class ItemDTO {
        private Long orderItemId;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
        private Product product;
        // Getters/setters
    }

    @Getter
    @Setter
    public static class Product {
        private String name;
        private String description;
        // Getters/setters
    }

    // Getters y setters principales
}