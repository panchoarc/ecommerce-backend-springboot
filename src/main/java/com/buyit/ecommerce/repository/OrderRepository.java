package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Order;
import com.buyit.ecommerce.entity.User;
import com.buyit.ecommerce.repository.projection.OrderVoucherProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);


    @Query(value = """
            SELECT 
            u.first_name || ' ' || u.last_name AS fullName,
            u.email AS email,
            a.street AS street,
            a.city AS city,
            a.country AS country,
            a.postal_code AS postalCode,
            ord.order_number AS orderNumber,
            ord.total_amount AS totalAmount,
            ord.status AS status,
            ord.created_at AS createdAt,
            ori.order_item_id AS orderItemId,
            ori.quantity AS quantity,
            ori.price_at_purchase AS priceAtPurchase,
            p.name AS productName,
            p.description AS productDescription
            FROM orders ord
            JOIN order_item ori ON ord.order_id = ori.order_id
            JOIN users u ON ord.user_id = u.user_id
            JOIN address a ON ord.address_id = a.address_id
            JOIN product p ON ori.product_id = p.product_id
            WHERE ord.order_number = :orderNumber
            AND u.keycloak_user_id = :userId
            ORDER BY ori.order_item_id
            """, nativeQuery = true)
    List<OrderVoucherProjection> findOrderDetails(@Param("orderNumber") String orderNumber, @Param("userId") String keycloakUserId);

    Optional<Order> findByOrOrderNumber(String orderNumber);
}
