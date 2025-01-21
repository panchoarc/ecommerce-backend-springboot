package com.buyit.ecommerce.dto.response.orderitem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long orderItemId;
    private int quantity;
    private BigDecimal priceAtPurchase;
}