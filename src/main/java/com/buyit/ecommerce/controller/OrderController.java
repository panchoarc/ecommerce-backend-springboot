package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.dto.request.order.CreateOrderRequest;
import com.buyit.ecommerce.dto.response.order.OrderResponse;
import com.buyit.ecommerce.dto.response.order.OrdersResponse;
import com.buyit.ecommerce.service.OrderService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<OrdersResponse>> getMyOrders(@AuthenticationPrincipal Jwt user,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {

        String keycloakUserId = userService.extractKeycloakIdFromUser(user);
        Page<OrdersResponse> myOrders = orderService.getMyOrders(keycloakUserId, page, size);

        Pagination pagination = ResponseBuilder.buildPagination(myOrders);
        return ResponseBuilder.successPaginated("Orders retrieved successfully", myOrders.getContent(), pagination);

    }

    @GetMapping("/{id}")
    public ApiResponse<OrdersResponse> getSpecificOrder(@AuthenticationPrincipal Jwt user,
                                                        @PathVariable("id") Long orderId) {

        String keycloakUserId = userService.extractKeycloakIdFromUser(user);
        OrdersResponse myOrder = orderService.getMyOrder(keycloakUserId, orderId);

        return ResponseBuilder.success("Order retrieved successfully", myOrder);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> addOrder(@AuthenticationPrincipal Jwt user, @Valid @RequestBody CreateOrderRequest requestedOrder) {

        String keycloakUserId = userService.extractKeycloakIdFromUser(user);
        OrderResponse orderResponse = orderService.createOrder(keycloakUserId, requestedOrder);
        return ResponseBuilder.success("Order created successfully", orderResponse);

    }
}
