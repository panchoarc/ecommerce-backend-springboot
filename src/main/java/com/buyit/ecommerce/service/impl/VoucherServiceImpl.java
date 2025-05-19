package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.dto.response.order.OrderDetailsDTO;
import com.buyit.ecommerce.exception.custom.ResourceNotFoundException;
import com.buyit.ecommerce.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public byte[] generateVoucher(String keycloakUserId, String orderNumber) {
        try {
            // Obtener datos de orden
            OrderDetailsDTO orderDetails = orderService.getVoucherData(keycloakUserId, orderNumber);

            // Generar QR
            String qrRedirect = frontendUrl + "/my-orders/" + orderNumber;
            String qrCode = qrCodeService.generateQRCodeImage(qrRedirect);

            // Generar HTML
            String html = voucherHtmlGeneratorService.generateVoucherHtml(orderDetails, qrCode);

            // Generar PDF
            byte[] pdfBytes = pdfGeneratorService.generateFromHtml(html);

            // Enviar correo
            emailService.sendOrderDocument(
                    orderDetails.getUser().getEmail(),
                    "Tu comprobante de orden #" + orderNumber,
                    "Adjunto encontrarás tu comprobante de pago. Escanea el QR para ver el detalle de tu orden.",
                    pdfBytes
            ).exceptionally(ex -> {
                log.error("Error enviando comprobante al correo: {}", ex.getMessage(), ex);
                return null;
            });

            return pdfBytes;

        } catch (Exception e) {
            log.error("Error generando y enviando comprobante", e);
            throw new ResourceNotFoundException("Error generando y enviando comprobante");
        }
    }
}
