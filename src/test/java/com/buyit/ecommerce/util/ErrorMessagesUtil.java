package com.buyit.ecommerce.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ErrorMessagesUtil {
    public Map<String, List<String>> getErrorMessages(ConstraintViolationException exception) {

        return exception.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())));
    }
}
