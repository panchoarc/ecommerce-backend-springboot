package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.order.CreateOrderRequest;
import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;
import com.buyit.ecommerce.dto.response.order.OrderDetailsResponse;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
    public OrderDetailsResponse getMyOrder(String keycloakUserId, String orderNumber) {
        User dbUser = userService.getUserByKeycloakId(keycloakUserId);
        Order order = getByOrderNumber(orderNumber);
        verifyOwnership(dbUser, order);

        return orderMapper.toOrderDetailsResponse(order);

    }

    @Override
    public OrderDetailsDTO getVoucherData(String keycloakUserId, String orderNumber) {


        User dbUser = userService.getUserByKeycloakId(keycloakUserId);

        List<Object[]> rows = orderRepository.findOrderDetails(orderNumber, dbUser.getUserId());

        if (rows.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }

        // Tomamos los campos comunes de la primera fila
        Object[] first = rows.get(0);
        OrderDetailsDTO dto = new OrderDetailsDTO();

        dto.setOrderNumber((String) first[6]);
        dto.setTotalAmount((BigDecimal) first[7]);
        dto.setStatus((String) first[8]);
        dto.setCreatedAt(((Timestamp) first[9]).toLocalDateTime());

        // User
        OrderDetailsDTO.UserDTO userDTO = new OrderDetailsDTO.UserDTO();
        userDTO.setFullName((String) first[0]);
        userDTO.setEmail((String) first[1]);
        dto.setUser(userDTO);

        // Address
        OrderDetailsDTO.AddressDTO addressDTO = new OrderDetailsDTO.AddressDTO();
        addressDTO.setStreet((String) first[2]);
        addressDTO.setCity((String) first[3]);
        addressDTO.setCountry((String) first[4]);
        addressDTO.setPostalCode((String) first[5]);
        dto.setAddress(addressDTO);

        // Items
        List<OrderDetailsDTO.ItemDTO> items = new ArrayList<>();
        for (Object[] row : rows) {
            OrderDetailsDTO.ItemDTO item = new OrderDetailsDTO.ItemDTO();
            item.setOrderItemId(((Number) row[10]).longValue());
            item.setQuantity(((Number) row[11]).intValue());
            item.setPriceAtPurchase((BigDecimal) row[12]);

            OrderDetailsDTO.Product product = new OrderDetailsDTO.Product();
            product.setName((String) row[13]);
            product.setDescription((String) row[14]);
            item.setProduct(product);

            items.add(item);
        }
        dto.setItems(items);

        return dto;
    }


    private Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
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
