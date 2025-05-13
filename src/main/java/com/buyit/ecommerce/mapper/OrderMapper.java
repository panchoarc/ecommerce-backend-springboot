package com.buyit.ecommerce.mapper;

import com.buyit.ecommerce.dto.request.product.BuyedProductResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.dto.response.order.OrderDetailsResponse;
import com.buyit.ecommerce.dto.response.order.OrderResponse;
import com.buyit.ecommerce.dto.response.order.OrdersResponse;
import com.buyit.ecommerce.entity.Address;
import com.buyit.ecommerce.entity.Order;
import com.buyit.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "address.addressId", target = "address.id")
    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "address", source = "address")
    OrderResponse toResponse(Order order);

    @Mapping(source = "address", target = "address")
    @Mapping(source = "orderItems", target = "products")
    OrderDetailsResponse toOrderDetailsResponse(Order order);


    @Mapping(source = "addressId", target = "id")
    UserAddressResponse addressToUserAddressResponse(Address address);


    @Mapping(source = "orderItems", target = "products")
    OrdersResponse toOrders(Order order);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "priceAtPurchase", target = "priceAtPurchase")
    BuyedProductResponse toBuyedProductResponse(OrderItem orderItem);

    List<BuyedProductResponse> toProductResponseList(List<OrderItem> orderItems);
}
