package com.buyit.ecommerce.exception;

import com.buyit.ecommerce.exception.custom.*;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(3)
public class BusinessExceptionHandler {

    private static final String MESSAGE = "message";

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseBuilder.error("Resource not found.", errors);
    }

    @ExceptionHandler(ResourceExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleResourceExistException(ResourceExistException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseBuilder.error("Resource already exists.", errors);
    }

    @ExceptionHandler(ResourceIllegalState.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleResourceIllegalState(ResourceIllegalState ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseBuilder.error("Illegal State.", errors);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInsufficientQuantityException(InsufficientQuantityException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());

        return ResponseBuilder.error("Insufficient quantity.", errors);
    }

    @ExceptionHandler(VoucherNotGeneratedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleVoucherNotGeneratedException(VoucherNotGeneratedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());

        return ResponseBuilder.error("There's an error occurred with voucher", errors);
    }

    @ExceptionHandler(EmailNotSendException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleEmailNotSendException(EmailNotSendException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());

        return ResponseBuilder.error("There's a error occurred with mail", errors);
    }

}
