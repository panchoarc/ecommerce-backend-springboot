package com.buyit.ecommerce.exception.custom;

public class VoucherNotGeneratedException extends RuntimeException {
    public VoucherNotGeneratedException(String message) {
        super(message);
    }
}
