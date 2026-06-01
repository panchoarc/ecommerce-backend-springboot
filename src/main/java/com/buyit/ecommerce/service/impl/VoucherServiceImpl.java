package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;
import com.buyit.ecommerce.service.*;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final OrderService orderService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;
    private final PdfGeneratorService pdfGeneratorService;

    private final VoucherHtmlGeneratorService voucherHtmlGeneratorService;


    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public byte[] generateVoucher(String keycloakUserId, String orderNumber) throws IOException, WriterException, MessagingException {

        OrderDetailsDTO orderDetails =
                orderService.getVoucherData(keycloakUserId, orderNumber);

        String qrRedirect = frontendUrl + "/my-orders/" + orderNumber;
        String qrCode = qrCodeService.generateQRCodeImage(qrRedirect);

        String html = voucherHtmlGeneratorService.generateVoucherHtml(orderDetails, qrCode);

        byte[] pdfBytes = pdfGeneratorService.generateFromHtml(html);

        // 🔥 IMPORTANTE: fire-and-forget (NO join)
        emailService.sendOrderDocument(
                orderDetails.getUser().getEmail(),
                "Tu comprobante de orden #" + orderNumber,
                "Adjunto encontrarás tu comprobante de pago.",
                pdfBytes
        ).exceptionally(ex -> {
            log.error("Error enviando email de voucher", ex);
            return null;
        });

        return pdfBytes;
    }
}
