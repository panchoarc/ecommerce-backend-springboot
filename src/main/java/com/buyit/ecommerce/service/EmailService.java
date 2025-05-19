package com.buyit.ecommerce.service;

import jakarta.mail.MessagingException;

import java.util.concurrent.CompletableFuture;

public interface EmailService {


    CompletableFuture<Void> sendOrderDocument(String to, String subject, String body, byte[] pdfData) throws MessagingException;
}
