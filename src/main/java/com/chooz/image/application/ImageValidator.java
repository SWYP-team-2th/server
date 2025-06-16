package com.chooz.image.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ImageValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final Set<String> allowedExtensions;

    public ImageValidator(@Value("${image.allowed-extensions}") Set<String> allowedExtensionsConfig) {
        this.allowedExtensions = allowedExtensionsConfig;
    }

    public void validate(String contentType) {
        if (!allowedExtensions.contains(contentType)) {
            throw new BadRequestException(ErrorCode.UNSUPPORTED_IMAGE_EXTENSION);
        }
    }
}