package com.buyit.ecommerce.exception.custom;

public class EmailNotSendException extends RuntimeException {
    public EmailNotSendException(String message) {
        super(message);
    }
}
