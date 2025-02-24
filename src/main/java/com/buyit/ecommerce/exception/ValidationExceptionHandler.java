package com.buyit.ecommerce.exception;

import com.buyit.ecommerce.util.ApiResponse;
import com.buyit.ecommerce.util.ResponseBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
@Order(1)
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        // Obtener la clase del objeto que causó la excepción
        Class<?> targetClass = Objects.requireNonNull(ex.getBindingResult().getTarget()).getClass();

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        log.info("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseBuilder.error("Check your JSON.", errors);
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

}
