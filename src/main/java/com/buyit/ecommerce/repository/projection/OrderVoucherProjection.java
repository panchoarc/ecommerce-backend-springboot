package com.buyit.ecommerce.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderVoucherProjection {

    String getFullName();
    String getEmail();

    String getStreet();
    String getCity();
    String getCountry();
    String getPostalCode();

    String getOrderNumber();
    BigDecimal getTotalAmount();
    String getStatus();
    LocalDateTime getCreatedAt();

    Long getOrderItemId();
    Integer getQuantity();
    BigDecimal getPriceAtPurchase();

    String getProductName();
    String getProductDescription();
}