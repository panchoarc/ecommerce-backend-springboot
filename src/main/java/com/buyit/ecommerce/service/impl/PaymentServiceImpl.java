package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.request.payments.CreatePaymentRequest;
import com.buyit.ecommerce.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secret_key}")
    private String stripeSecretKey;

    @Override
    public String createPaymentIntent(CreatePaymentRequest paymentRequest) throws StripeException {
        // Asignamos la clave de API a Stripe
        Stripe.apiKey = stripeSecretKey;

        // Creamos los parámetros para el PaymentIntent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount()) // en centavos
                .setCurrency("usd")
                .build();

        // Creamos el PaymentIntent y obtenemos el client secret
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        log.info("Payment intent created {}", paymentIntent.getClientSecret());
        return paymentIntent.getClientSecret();
    }
}
