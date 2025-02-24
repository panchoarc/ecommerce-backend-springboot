package com.buyit.ecommerce.exception;

import com.buyit.ecommerce.exception.custom.KeycloakIntegrationException;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@Order(4)
public class GlobalExceptionHandler {


    @ExceptionHandler(KeycloakIntegrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleKeycloakIntegrationException(KeycloakIntegrationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return ResponseBuilder.error("Keycloak integration error.", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, String> errorDetail = Collections.singletonMap("error", "Please contact support if the issue persists.");
        return ResponseBuilder.error("An unexpected error occurred.", errorDetail);
    }
}
