package com.buyit.ecommerce.exception.custom;

public class ResourceExistException extends RuntimeException {
    public ResourceExistException(String message) {
        super(message);
    }

    public ResourceExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
