package com.buyit.ecommerce.exception.custom;

public class TooManyRequestException extends RuntimeException {
    public TooManyRequestException(String message) {
        super(message);
    }
}
