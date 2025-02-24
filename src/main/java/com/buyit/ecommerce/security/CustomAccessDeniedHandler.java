package com.buyit.ecommerce.security;

import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver exceptionResolver;

    public CustomAccessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        log.info("CustomAccessDeniedHandler EXCEPTION: {}", accessDeniedException.getMessage());

        // Usar un mensaje más descriptivo que sea claro para el usuario
        String message = "You cannot access this resource.";

        DeniedAccessException exception = new DeniedAccessException(message);

        exceptionResolver.resolveException(request, response, null, exception);
    }
}
