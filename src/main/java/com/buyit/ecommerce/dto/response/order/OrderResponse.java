package com.buyit.ecommerce.dto.response.order;


import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.buyit.ecommerce.dto.response.orderitem.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private UserAddressResponse address;
    private List<OrderItemResponse> orderItems;


}
