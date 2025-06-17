package com.chooz.image.util;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.image.application.ImageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageValidatorTest {

    private ImageValidator imageValidator;

    @BeforeEach
    void setUp() {
        Set<String> allowedExtensions = Set.of("gif" ,"jpg", "jpeg", "png", "webp", "heic", "heif");
        imageValidator = new ImageValidator(allowedExtensions);
    }

    @Test
    @DisplayName("파일 유효성 체크 - 지원하지 않는 확장자")
    void validate_unsupportedContentType() {
        // given
        String unsupportedContentType = "txt";

        // when then
        assertThatThrownBy(() -> imageValidator.validate(unsupportedContentType))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.UNSUPPORTED_IMAGE_EXTENSION.getMessage());
    }
}
