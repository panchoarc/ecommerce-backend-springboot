package com.buyit.ecommerce.anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageValidator implements ConstraintValidator<ValidImage, List<MultipartFile>> {


    private static final String[] ALLOWED_FORMATS = {"image/jpeg", "image/png", "image/gif"};

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {

        if (files == null) {
            return true;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                return false;
            }

            String mimeType = file.getContentType();
            if (mimeType == null || !isAllowedFormat(mimeType)) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllowedFormat(String mimeType) {
        for (String format : ALLOWED_FORMATS) {
            if (mimeType.equals(format)) {
                return true;
            }
        }
        return false;
    }
}
