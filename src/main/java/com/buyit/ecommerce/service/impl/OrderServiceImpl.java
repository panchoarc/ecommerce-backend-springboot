package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.order.CreateOrderRequest;
import com.buyit.ecommerce.dto.response.order.OrderResponse;
import com.buyit.ecommerce.dto.response.order.OrdersResponse;
import com.buyit.ecommerce.entity.Address;
import com.buyit.ecommerce.entity.Order;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.mapper.OrderMapper;
import com.buyit.ecommerce.repository.OrderRepository;
import com.buyit.ecommerce.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {


    private final UserService userService;
    private final AddressService addressService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;


    @Override
    public OrderResponse createOrder(String keycloakUserId, CreateOrderRequest requestedOrder) {

        log.info("Request received for order creation: {}", requestedOrder);

        Address userAddress = addressService.getMyAddress(keycloakUserId, requestedOrder.getAddressId());
        User dbUser = userService.getUserByKeycloakId(keycloakUserId);


        productService.updateStockForOrder(requestedOrder.getCartItems());
        BigDecimal totalOrderAmount = productService.calculateTotalPrice(requestedOrder.getCartItems());
        String orderNumber = UUID.randomUUID().toString();

        // 5. Crear el objeto Order
        Order newOrder = new Order();
        newOrder.setUser(dbUser);
        newOrder.setOrderNumber(orderNumber);
        newOrder.setAddress(userAddress);
        newOrder.setTotalAmount(totalOrderAmount);
        newOrder.setStatus("PENDING");


        Order savedOrder = orderRepository.save(newOrder);
        log.info("Order created: {}", savedOrder);

        orderItemService.createOrderItems(requestedOrder.getCartItems(), savedOrder);

        // Mapear la entidad Order a la respuesta DTO
        OrderResponse orderResponse = orderMapper.toResponse(savedOrder);

        log.info("Order created successfully: {}", newOrder);

        return orderResponse;
    }

    @Override
    public Page<OrdersResponse> getMyOrders(String keycloakUserId, int page, int size) {

        Sort sort = Sort.by(Sort.Direction.ASC, "orderId");
        Pageable pageable = PageRequest.of(page, size, sort);

        User dbUser = userService.getUserByKeycloakId(keycloakUserId);

        Page<Order> orderPage = orderRepository.findAllByUser(dbUser, pageable);

        return orderPage.map(orderMapper::toOrders);
    }

    @Override
    public OrdersResponse getMyOrder(String keycloakUserId, Long orderId) {
        User dbUser = userService.getUserByKeycloakId(keycloakUserId);
        Order order = getOrder(orderId);
        verifyOwnership(dbUser, order);

        return orderMapper.toOrders(order);

    }


    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public void verifyOwnership(User user, Order order) {

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new DeniedAccessException("You are not allowed to delete this review");
        }
    }
}
