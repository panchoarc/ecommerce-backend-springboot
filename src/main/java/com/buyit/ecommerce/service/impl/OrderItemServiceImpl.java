package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.order.ProductOrderRequest;
import com.buyit.ecommerce.entity.Order;
import com.buyit.ecommerce.entity.OrderItem;
import com.buyit.ecommerce.entity.Product;
import com.buyit.ecommerce.repository.OrderItemRepository;
import com.buyit.ecommerce.repository.OrderRepository;
import com.buyit.ecommerce.service.OrderItemService;
import com.buyit.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;


    @Override
    public void createOrderItems(List<ProductOrderRequest> cartItems, Order order) {


        List<OrderItem> orderItems = new ArrayList<>();
        cartItems.forEach(cartItem -> {
            // Obtener el producto basado en el ID del cartItem
            Long productId = cartItem.getProductId();
            Product product = productService.getProduct(productId);

            // Crear un OrderItem con el producto, la cantidad y el precio
            int quantity = cartItem.getQuantity();
            BigDecimal price = product.getPrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setOrder(order); // Asegúrate de establecer la relación bidireccional correctamente
            orderItem.setPriceAtPurchase(price);

            orderItems.add(orderItem); // Añadir a la lista
        });

        // Si deseas optimizar la persistencia, puedes hacer una única llamada para guardar todos los items
        orderItemRepository.saveAll(orderItems);

        order.setOrderItems(orderItems); // Establecer los OrderItems en el Order
        orderRepository.save(order);


        log.info("Finished creating order items for order: {}", order.getOrderId());

    }
}
