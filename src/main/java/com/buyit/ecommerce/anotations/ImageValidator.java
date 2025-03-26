package com.buyit.ecommerce.anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageValidator implements ConstraintValidator<ValidImage, Object> {

    private static final String[] ALLOWED_FORMATS = {"image/jpeg", "image/png", "image/gif"};

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // No se permiten valores nulos
        }

        if (value instanceof MultipartFile file) {
            return isValidFile(file);
        } else if (value instanceof List<?> fileList) {
            return isValidFileList(fileList);
        }

        return false; // Tipo no compatible
    }

    private boolean isValidFile(MultipartFile file) {
        return file != null && !file.isEmpty() && isAllowedFormat(file.getContentType());
    }

    private boolean isValidFileList(List<?> files) {
        if (files.isEmpty()) {
            return false; // Debe haber al menos un archivo
        }

        for (Object obj : files) {
            if (!(obj instanceof MultipartFile file) || !isValidFile(file)) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllowedFormat(String mimeType) {
        if (mimeType == null) return false;
        for (String format : ALLOWED_FORMATS) {
            if (mimeType.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }
}
