package com.buyit.ecommerce.service;

import com.buyit.ecommerce.dto.request.payments.CreatePaymentRequest;
import com.stripe.exception.StripeException;

public interface PaymentService {

    String createPaymentIntent(CreatePaymentRequest amount) throws StripeException;
}
