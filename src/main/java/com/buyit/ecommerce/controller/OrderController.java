package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.dto.request.order.CreateOrderRequest;
import com.buyit.ecommerce.dto.response.order.OrderDetailsResponse;
import com.buyit.ecommerce.dto.response.order.OrderResponse;
import com.buyit.ecommerce.dto.response.order.OrdersResponse;
import com.buyit.ecommerce.service.OrderService;
import com.buyit.ecommerce.service.UserService;
import com.buyit.ecommerce.service.VoucherService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.Pagination;
import com.buyit.ecommerce.util.ResponseBuilder;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private final VoucherService voucherService;

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
    public ApiResponse<OrderDetailsResponse> getSpecificOrder(@AuthenticationPrincipal Jwt user,
                                                        @PathVariable("id") String orderNumber) {

        String keycloakUserId = userService.extractKeycloakIdFromUser(user);
        OrderDetailsResponse myOrder = orderService.getMyOrder(keycloakUserId, orderNumber);

        return ResponseBuilder.success("Order retrieved successfully", myOrder);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> addOrder(@AuthenticationPrincipal Jwt user, @Valid @RequestBody CreateOrderRequest requestedOrder) {

        String keycloakUserId = userService.extractKeycloakIdFromUser(user);
        OrderResponse orderResponse = orderService.createOrder(keycloakUserId, requestedOrder);
        return ResponseBuilder.success("Order created successfully", orderResponse);

    }


    @GetMapping("/{id}/vouchers")
    public ResponseEntity<byte[]> getVoucher(@AuthenticationPrincipal Jwt user, @PathVariable("id") String orderNumber) throws IOException, WriterException {

        String keycloakUserId = userService.extractKeycloakIdFromUser(user);
        byte[] pdf = voucherService.generateVoucher(keycloakUserId, orderNumber);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=voucher.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
