package com.chooz.post.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ShareUrlServiceTest {

    ShareUrlService shareUrlService;

    ShareUrlKeyGenerator shareUrlKeyGenerator;

    @BeforeEach
    void setUp() {
        shareUrlKeyGenerator = mock(ShareUrlKeyGenerator.class);
        shareUrlService = new ShareUrlService(shareUrlKeyGenerator);
    }

    @Test
    @DisplayName("공유 url 생성 및 키 조회")
    void generateShareUrl() throws Exception {
        //given
        String shareUrlKey = "174822695935299";
        given(shareUrlKeyGenerator.generateKey())
                .willReturn(shareUrlKey);

        //when then
        String shareUrl = shareUrlService.generateShareUrl();
        System.out.println("shareUrl = " + shareUrl);
        System.out.println("shareUrl.length() = " + shareUrl.length());
        assertThat(shareUrl).isNotNull();
    }
}
