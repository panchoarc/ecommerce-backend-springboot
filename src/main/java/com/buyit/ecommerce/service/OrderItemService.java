package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.order.ProductOrderRequest;
import com.buyit.ecommerce.entity.Order;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface OrderItemService {

    void createOrderItems(@NotEmpty List<ProductOrderRequest> cartItems, Order order);
}
