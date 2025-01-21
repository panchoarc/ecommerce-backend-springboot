package com.buyit.ecommerce.exception;

import com.buyit.ecommerce.exception.custom.*;
import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MESSAGE_FIELD = "message";

    // Método común para crear respuestas de error
    private ApiResponse<Void> buildErrorResponse(String message, String detailMessage) {
        Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put(MESSAGE_FIELD, detailMessage);
        return ResponseBuilder.error(message, errorDetail);
    }

    // Método para obtener el nombre de la propiedad JSON
    private String getJsonPropertyName(Class<?> targetClass, String fieldName) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

            if (jsonProperty != null) {
                return jsonProperty.value(); // Retorna el valor de @JsonProperty
            }
        } catch (NoSuchFieldException ignored) {
            // Si el campo no existe en la clase, continuar
        }
        return null; // Retorna null si no encuentra @JsonProperty
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        // Obtener la clase del objeto que causó la excepción
        Class<?> targetClass = ex.getBindingResult().getTarget().getClass();

        // Iterar sobre los errores de campo
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String fieldName = fieldError.getField();
            String jsonPropertyName = getJsonPropertyName(targetClass, fieldName);

            if (jsonPropertyName == null) {
                // Si no tiene @JsonProperty, usar el nombre del campo original
                jsonPropertyName = fieldName;
            }

            validationErrors.put(jsonPropertyName, fieldError.getDefaultMessage());
        }

        log.warn("Validation error: {}", validationErrors);
        return ResponseBuilder.error("Validation failed.", validationErrors);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse("Resource not found.", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String paramType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "Unknown";
        String invalidValue = (ex.getValue() != null) ? ex.getValue().toString() : "null";

        log.warn("Type mismatch error: Parameter '{}' should be of type '{}', but got '{}'", paramName, paramType, invalidValue);

        Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put("parameter", paramName);
        errorDetail.put("expectedType", paramType);
        errorDetail.put("invalidValue", invalidValue);
        errorDetail.put("hint", "Ensure the value is compatible with the required type.");

        return ResponseBuilder.error("Invalid parameter type.", errorDetail);
    }

    @ExceptionHandler(KeycloakIntegrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleKeycloakIntegrationException(KeycloakIntegrationException ex) {
        return buildErrorResponse("Keycloak integration error.", ex.getMessage());
    }

    @ExceptionHandler(ResourceExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleResourceExistException(ResourceExistException ex) {
        return buildErrorResponse("Resource already exists.", ex.getMessage());
    }

    @ExceptionHandler(ResourceIllegalState.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleResourceIllegalState(ResourceIllegalState ex) {
        return buildErrorResponse("Illegal State.", ex.getMessage());
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInsufficientQuantityException(InsufficientQuantityException ex) {
        return buildErrorResponse("Insufficient quantity.", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.info("HttpMessageNotReadableException: {}", ex.getMessage());
        return buildErrorResponse("Check your JSON.", "Cannot deserialize your JSON. Check if the values are correct.");
    }

    @ExceptionHandler(DeniedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleDeniedAccessException(DeniedAccessException ex) {
        log.info("AccessDeniedException: {}", ex.getMessage());
        return buildErrorResponse("Access Denied", ex.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnAuthorizedException(UnAuthorizedException ex) {
        log.info("UnAuthorizedException: {}", ex.getMessage());
        return buildErrorResponse("Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, String> errorDetail = Collections.singletonMap("error", "Please contact support if the issue persists.");
        return ResponseBuilder.error("An unexpected error occurred.", errorDetail);
    }
}
