package com.buyit.ecommerce.controller;


import com.buyit.ecommerce.dto.request.payments.CreatePaymentRequest;
import com.buyit.ecommerce.service.PaymentService;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/create")
    public ApiResponse<String> createPaymentIntent(@RequestBody CreatePaymentRequest paymentRequest) throws StripeException {
        String paymentIntent = paymentService.createPaymentIntent(paymentRequest);
        return ResponseBuilder.success("Create intent", paymentIntent);
    }

}
