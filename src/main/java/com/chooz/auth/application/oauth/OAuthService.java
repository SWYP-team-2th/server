package com.chooz.auth.application.oauth;

import com.chooz.auth.application.oauth.dto.KakaoAuthResponse;
import com.chooz.auth.application.oauth.dto.OAuthUserInfo;
import com.chooz.common.config.KakaoOAuthConfig;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private static final String BEARER = "Bearer ";

    private final KakaoOAuthConfig kakaoOAuthConfig;
    private final KakaoOAuthClient kakaoOAuthClient;

    public OAuthUserInfo getUserInfo(String code, String redirectUri) {
        try {
            KakaoAuthResponse kakaoAuthResponse = kakaoOAuthClient.fetchToken(tokenRequestParams(code, redirectUri));
            log.info("getUserInfo kakaoAuthResponse: {}", kakaoAuthResponse);
            return kakaoOAuthClient
                    .fetchUserInfo(BEARER + kakaoAuthResponse.accessToken())
                    .toOAuthUserInfo();
        } catch (Exception e) {
            log.debug("소셜 로그인 실패 {}", e.getMessage());
            throw new BadRequestException(ErrorCode.SOCIAL_AUTHENTICATION_FAILED);
        }
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode, String redirectUri) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthConfig.clientId());
        params.add("redirect_uri", redirectUri);
        params.add("code", authCode);
        params.add("client_secret", kakaoOAuthConfig.clientSecret());
        return params;
    }
}
