package com.chooz.auth.application;

import com.chooz.auth.application.jwt.JwtProvider;
import com.chooz.auth.application.jwt.TokenPair;
import com.chooz.auth.application.oauth.OAuthService;
import com.chooz.auth.application.oauth.dto.OAuthUserInfo;
import com.chooz.auth.domain.Provider;
import com.chooz.auth.domain.SocialAccount;
import com.chooz.auth.domain.SocialAccountRepository;
import com.chooz.auth.presentation.dto.TokenResponse;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    AuthService authService;

    @MockitoBean
    OAuthService oAuthService;

    @MockitoBean
    JwtProvider jwtProvider;

    @Autowired
    SocialAccountRepository socialAccountRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("OAuth 로그인하면 토큰 발급해야 하고 유저 정보 없는 경우 유저 생성")
    void oAuthSignIn() throws Exception {
        //given
        OAuthUserInfo oAuthUserInfo = new OAuthUserInfo("socialId", "profileImageUrl", "nickname", Provider.KAKAO);
        given(oAuthService.getUserInfo(anyString(), anyString()))
                .willReturn(oAuthUserInfo);
        TokenPair expectedTokenPair = new TokenPair("accessToken", "refreshToken");
        given(jwtProvider.createToken(any()))
                .willReturn(expectedTokenPair);

        //when
        TokenResponse tokenResponse = authService.oauthSignIn("code", "https://dev.chooz.site");

        //then
        TokenPair tokenPair = tokenResponse.tokenPair();
        SocialAccount socialAccount = socialAccountRepository.findBySocialIdAndProvider(oAuthUserInfo.socialId(), Provider.KAKAO).get();
        User user = userRepository.findById(socialAccount.getUserId()).get();
        assertAll(
                () -> assertThat(tokenPair).isEqualTo(expectedTokenPair),
                () -> assertThat(socialAccount.getUserId()).isNotNull(),
                () -> assertThat(socialAccount.getSocialId()).isEqualTo(oAuthUserInfo.socialId()),
                () -> assertThat(socialAccount.getProvider()).isEqualTo(oAuthUserInfo.provider()),
                () -> assertThat(user.getNickname()).isEqualTo(oAuthUserInfo.nickname()),
                () -> assertThat(user.getProfileUrl()).isEqualTo(oAuthUserInfo.profileImageUrl())
        );
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void withdraw() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());

        // when
        authService.withdraw(user.getId());

        // then
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isEmpty();
    }
}
