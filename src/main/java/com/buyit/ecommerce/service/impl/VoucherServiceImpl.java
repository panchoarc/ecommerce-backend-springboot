package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;
import com.buyit.ecommerce.service.OrderService;
import com.buyit.ecommerce.service.QRCodeService;
import com.buyit.ecommerce.service.VoucherService;
import com.google.zxing.WriterException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final OrderService orderService;
    private final TemplateEngine templateEngine;
    private final QRCodeService qrCodeService;

    @Override
    public byte[] generateVoucher(String keycloakUserId, String orderNumber) throws IOException, WriterException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        OrderDetailsDTO response = orderService.getVoucherData(keycloakUserId, orderNumber);

        String qrRedirect = "CUALQUIER COSA";

        String qrcode = qrCodeService.generateQRCodeImage(qrRedirect);

        log.info("Voucher generated for orderNumber: {}", orderNumber);
        log.info("Orders: {}", response);

        Context context = new Context();

        context.setVariable("orderId", response.getOrderNumber());
        context.setVariable("orderNumber", orderNumber);
// Obtener la fecha y la hora por separado
        context.setVariable("date", response.getCreatedAt().toLocalDate().format(dateFormatter));
        context.setVariable("time", response.getCreatedAt().toLocalTime().format(timeFormatter));
        context.setVariable("items", response.getItems());
        context.setVariable("customerName", response.getUser().getFullName());
        context.setVariable("customerEmail", response.getUser().getEmail());
        context.setVariable("total", response.getTotalAmount());
        context.setVariable("shippingAddress", response.getAddress().getStreet());
        context.setVariable("shippingCity", response.getAddress().getCity());
        context.setVariable("shippingZip", response.getAddress().getPostalCode());
        context.setVariable("shippingCountry", response.getAddress().getCountry());
        context.setVariable("paymentMethod", "Card");
        context.setVariable("paymentStatus", response.getStatus());
        context.setVariable("qrCode", qrcode);

        String html = templateEngine.process("voucher", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setBaseUri("classpath:/templates/");
            HtmlConverter.convertToPdf(html, outputStream, converterProperties);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}
