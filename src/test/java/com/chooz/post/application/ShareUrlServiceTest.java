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
        Base62Encryptor encryptor = new Base62Encryptor("asdfd", "1541235432");
        shareUrlService = new ShareUrlService(shareUrlKeyGenerator, encryptor);
    }

    @Test
    @DisplayName("공유 url 생성 및 키 조회")
    void generateShareUrl() throws Exception {
        //given
        String shareUrlKey = "shareUrlKey";
        given(shareUrlKeyGenerator.generateKey())
                .willReturn(shareUrlKey);

        //when then
        String shareUrl = shareUrlService.generateShareUrl();
        assertThat(shareUrl).isNotNull();

        String key = shareUrlService.getShareUrlKey(shareUrl);
        assertThat(key).isEqualTo(shareUrlKey);
    }
}
