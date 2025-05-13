package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.order.CreateOrderRequest;
import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;
import com.buyit.ecommerce.dto.response.order.OrderDetailsResponse;
import com.buyit.ecommerce.dto.response.order.OrderResponse;
import com.buyit.ecommerce.dto.response.order.OrdersResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse createOrder(String keycloakUserId, @Valid CreateOrderRequest requestedOrder);

    Page<OrdersResponse> getMyOrders(String keycloakUserId, int page, int size);

    OrderDetailsResponse getMyOrder(String keycloakUserId, String orderNumber);

    OrderDetailsDTO getVoucherData(String keycloakUserId, String orderNumber);

}
