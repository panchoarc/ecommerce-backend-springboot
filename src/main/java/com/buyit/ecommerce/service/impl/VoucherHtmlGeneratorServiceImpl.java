package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;
import com.buyit.ecommerce.service.VoucherHtmlGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class VoucherHtmlGeneratorServiceImpl implements VoucherHtmlGeneratorService {


    private final TemplateEngine templateEngine;

    @Override
    public String generateVoucherHtml(OrderDetailsDTO order, String qrCode) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        Context context = new Context();
        context.setVariable("orderId", order.getOrderNumber());
        context.setVariable("orderNumber", order.getOrderNumber());
        context.setVariable("date", order.getCreatedAt().toLocalDate().format(dateFormatter));
        context.setVariable("time", order.getCreatedAt().toLocalTime().format(timeFormatter));
        context.setVariable("items", order.getItems());
        context.setVariable("customerName", order.getUser().getFullName());
        context.setVariable("customerEmail", order.getUser().getEmail());
        context.setVariable("total", order.getTotalAmount());
        context.setVariable("shippingAddress", order.getAddress().getStreet());
        context.setVariable("shippingCity", order.getAddress().getCity());
        context.setVariable("shippingZip", order.getAddress().getPostalCode());
        context.setVariable("shippingCountry", order.getAddress().getCountry());
        context.setVariable("paymentMethod", "Card");
        context.setVariable("paymentStatus", order.getStatus());
        context.setVariable("qrCode", qrCode);

        return templateEngine.process("voucher", context);
    }
}
