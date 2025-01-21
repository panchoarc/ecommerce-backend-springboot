package com.buyit.ecommerce.dto.request.order;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest implements Serializable {

    @NotEmpty
    @JsonProperty("cart_items")
    private transient List<ProductOrderRequest> cartItems;

    @NotNull
    @JsonProperty("address_id")
    private Long addressId;

}
