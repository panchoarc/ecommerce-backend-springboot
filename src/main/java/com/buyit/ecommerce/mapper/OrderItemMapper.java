package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.response.orderitem.OrderItemResponse;
import com.buyit.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // Convertir OrderItem a OrderItemResponse
    OrderItemResponse toResponse(OrderItem orderItem);
}