package com.chooz.image.presentation;

import com.chooz.image.presentation.dto.PresignedUrlRequest;
import com.chooz.image.presentation.dto.PresignedUrlResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("이미지 업로드")
    void createPresignedUrl() throws Exception {
        //given
        PresignedUrlRequest request = new PresignedUrlRequest(12345L, "image/jpg");
        PresignedUrlResponse response = new PresignedUrlResponse(
                "https://presigned-url.com/upload-url",
                "https://example.com/images/image.jpg",
                "images/image.jpg"
        );
        when(imageService.getPresignedUrl(any(PresignedUrlRequest.class)))
                .thenReturn(response);

        //when then
        mockMvc.perform(MockMvcRequestBuilders.post("/image/upload")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        requestFields(
                                fieldWithPath("contentType")
                                        .description("이미지의 Content-Type (예: image/jpg)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("contentLength")
                                        .description("이미지 파일 크기 (바이트 단위)")
                                        .type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("signedUploadPutUrl")
                                        .description("이미지 업로드 presigned URL (이미지를 해당 URL로 PUT 요청을 보내야 함, 만료 시간 5분)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("signedGetUrl")
                                        .description("이미지 조회 전체 주소")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("assetUrl")
                                        .description("이미지 저장 경로")
                                        .type(JsonFieldType.STRING)
                        )
                ));
    }
}
