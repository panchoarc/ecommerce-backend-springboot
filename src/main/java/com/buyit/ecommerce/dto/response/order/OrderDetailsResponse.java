package com.buyit.ecommerce.dto.response.order;

import com.buyit.ecommerce.dto.request.product.BuyedProductResponse;
import com.buyit.ecommerce.dto.response.address.UserAddressResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailsResponse {

    @JsonProperty("id")
    private Long orderId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("products")
    List<BuyedProductResponse> products;


    @JsonProperty("address")
    private UserAddressResponse address;
}
