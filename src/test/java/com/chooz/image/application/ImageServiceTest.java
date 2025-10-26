package com.chooz.image.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.image.application.dto.PresignedUrlRequestDto;
import com.chooz.image.presentation.dto.PresignedUrlRequest;
import com.chooz.image.presentation.dto.PresignedUrlResponse;
import com.chooz.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class ImageServiceTest extends IntegrationTest {

    @Autowired
    ImageService imageService;

    @MockitoBean
    S3Client s3Client;

    @MockitoBean
    ImageNameGenerator imageNameGenerator;

    @Autowired
    ImageProperties imageProperties;

    @Test
    @DisplayName("presigned url 생성")
    void getPresignedUrl() throws Exception {
        //given
        PresignedUrlRequest request = new PresignedUrlRequest(12345L, "image/jpeg");
        String presignedUrl = "https://example.com/presigned-url";
        String imageName = "test-image";
        given(s3Client.getPresignedPutUrl(any(PresignedUrlRequestDto.class)))
                .willReturn(presignedUrl);
        given(imageNameGenerator.generate())
                .willReturn(imageName);

        //when
        PresignedUrlResponse response = imageService.getPresignedUrl(request);

        //then
        assertAll(
                () -> assertThat(response.signedUploadPutUrl()).isEqualTo(presignedUrl),
                () -> assertThat(response.signedGetUrl()).isEqualTo(imageProperties.endpoint() + imageProperties.path() + imageName),
                () -> assertThat(response.assetUrl()).isEqualTo(imageProperties.path() + imageName)
        );
    }

    @Test
    @DisplayName("presigned url 생성 - 지원하지 않는 컨텐츠 타입")
    void getPresignedUrl_unsupportedContentType() throws Exception {
        //given
        PresignedUrlRequest request = new PresignedUrlRequest(12345L, "unsupported/type");

        //when then
        assertThatThrownBy(() -> imageService.getPresignedUrl(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.UNSUPPORTED_IMAGE_EXTENSION.getMessage());
    }
}
