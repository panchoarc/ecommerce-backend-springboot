package com.buyit.ecommerce.exception.custom;

public class ResourceIllegalState extends RuntimeException {
    public ResourceIllegalState(String message) {
        super(message);
    }

    public ResourceIllegalState(String message, Throwable cause) {
        super(message, cause);
    }
}
