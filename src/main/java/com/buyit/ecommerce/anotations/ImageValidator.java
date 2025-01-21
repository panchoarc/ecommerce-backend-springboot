package com.buyit.ecommerce.anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile[]> {

    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {

        // Si el array de archivos es nulo o vacío, se considera válido (podría ser opcional)
        if (files == null) {
            return true;
        }

        // Verificar cada archivo en el array
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                return false; // Si hay algún archivo vacío, no es válido
            }

            String mimeType = file.getContentType();
            if (mimeType == null || !mimeType.startsWith("image/")) {
                return false; // Si el archivo no es una imagen, no es válido
            }
        }

        return true; // Todos los archivos son válidos
    }

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

}
