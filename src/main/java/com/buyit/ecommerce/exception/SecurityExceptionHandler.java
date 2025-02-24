package com.buyit.ecommerce.exception;

import com.buyit.ecommerce.exception.custom.AuthenticationException;
import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.exception.custom.UnAuthorizedException;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@Order(2)
public class SecurityExceptionHandler {


    @ExceptionHandler({AuthenticationException.class, UnAuthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthenticationException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.error("Authentication Exception: {}", ex.getMessage());
        return ResponseBuilder.error("Authentication Failed", errors);
    }

    @ExceptionHandler(DeniedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleDeniedAccessException(DeniedAccessException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        log.info("AccessDeniedException: {}", ex.getMessage());
        return ResponseBuilder.error("Access Denied", errors);
    }
}
