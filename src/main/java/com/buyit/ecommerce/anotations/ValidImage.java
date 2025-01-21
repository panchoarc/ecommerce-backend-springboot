package com.buyit.ecommerce.anotations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {

    String message() default "File isn't a valid image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
