package com.buyit.ecommerce.service.impl;

import com.buyit.ecommerce.exception.custom.EmailNotSendException;
import com.buyit.ecommerce.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {


    private final JavaMailSender mailSender;


    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> sendOrderDocument(String toEmail, String subject, String body, byte[] pdfData) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            ByteArrayDataSource pdfBytes = new ByteArrayDataSource(pdfData, MediaType.APPLICATION_PDF_VALUE);
            helper.addAttachment("ComprobanteOrder.pdf", pdfBytes);

            mailSender.send(mimeMessage);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            // Aquí lanzas una excepción custom que será capturada por ControllerAdvice si haces join()
            return CompletableFuture.failedFuture(new EmailNotSendException("Fallo al enviar el correo"));
        }
    }
}
