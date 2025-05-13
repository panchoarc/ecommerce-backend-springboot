package com.buyit.ecommerce.dto.request.payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreatePaymentRequest {

    private Long amount;
}
