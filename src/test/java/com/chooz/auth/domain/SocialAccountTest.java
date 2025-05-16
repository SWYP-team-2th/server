package com.chooz.auth.domain;

import com.chooz.auth.application.oauth.dto.OAuthUserInfo;
import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SocialAccountTest {


    @Test
    @DisplayName("SocialAccount Entity 생성")
    void create() throws Exception {
        //given
        long givenUserId = 1L;
        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo(
                "socialId",
                "profileImageUrl",
                "nickname",
                Provider.KAKAO
        );

        //when
        SocialAccount socialAccount = SocialAccount.create(givenUserId, oAuthUserInfo);

        //then
        assertAll(
                () -> assertThat(socialAccount.getUserId()).isEqualTo(givenUserId),
                () -> assertThat(socialAccount.getSocialId()).isEqualTo(oAuthUserInfo.socialId()),
                () -> assertThat(socialAccount.getProvider()).isEqualTo(oAuthUserInfo.provider())
        );
    }

    @Test
    @DisplayName("SocialAccount Entity 생성 - 파라미터가 null인 경우")
    void create_null() throws Exception {
        //given

        //when then
        assertAll(
                () -> assertThatThrownBy(() -> SocialAccount.create(1L, null))
                        .isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage()),
                () -> assertThatThrownBy(() -> SocialAccount.create(
                        1L,
                        new OAuthUserInfo(null, "profileImageUrl", "nickname", Provider.KAKAO)
                )).isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage()),
                () -> assertThatThrownBy(() -> SocialAccount.create(
                        1L,
                        new OAuthUserInfo("socialId", "profileImageUrl", "nickname", null)
                )).isInstanceOf(InternalServerException.class)
                        .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage())
        );
    }
}
