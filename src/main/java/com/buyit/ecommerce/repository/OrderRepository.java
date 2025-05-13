package com.buyit.ecommerce.repository;

import com.buyit.ecommerce.entity.Order;
import com.buyit.ecommerce.entity.User;
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


    @Query(value = "SELECT " +
            "u.first_name || ' ' || u.last_name AS full_name, " +
            "u.email, " +
            "a.street, " +
            "a.city, " +
            "a.country, " +
            "a.postal_code, " +
            "ord.order_number, " +
            "ord.total_amount, " +
            "ord.status, " +
            "ord.created_at, " +
            "ori.order_item_id, " +
            "ori.quantity, " +
            "ori.price_at_purchase, " +
            "p.name AS product_name, " +
            "p.description AS product_description " +
            "FROM orders ord " +
            "JOIN order_item ori ON ord.order_id = ori.order_id " +
            "JOIN users u ON ord.user_id = u.user_id " +
            "JOIN address a ON ord.address_id = a.address_id " +
            "JOIN product p ON ori.product_id = p.product_id " +
            "WHERE ord.order_number = :orderNumber " +
            "AND u.user_id = :userId " +
            "ORDER BY ord.order_id, ori.order_item_id", nativeQuery = true)
    List<Object[]> findOrderDetails(@Param("orderNumber") String orderNumber, @Param("userId") Long userId);

    Optional<Order> findByOrOrderNumber(String orderNumber);
}
