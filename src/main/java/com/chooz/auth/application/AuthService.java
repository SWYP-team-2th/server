package com.chooz.auth.application;

import com.chooz.auth.application.jwt.JwtClaim;
import com.chooz.auth.application.jwt.JwtService;
import com.chooz.auth.application.oauth.OAuthService;
import com.chooz.auth.application.oauth.dto.OAuthUserInfo;
import com.chooz.auth.domain.Provider;
import com.chooz.auth.domain.SocialAccount;
import com.chooz.auth.domain.SocialAccountRepository;
import com.chooz.auth.presentation.dto.TokenResponse;
import com.chooz.user.application.UserService;
import com.chooz.user.domain.Role;
import com.chooz.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final OAuthService oAuthService;
    private final SocialAccountRepository socialAccountRepository;
    private final UserService userService;

    public TokenResponse oauthSignIn(String code, String redirectUri) {
        OAuthUserInfo oAuthUserInfo = oAuthService.getUserInfo(code, redirectUri);
        SocialAccount socialAccount = getSocialAccount(oAuthUserInfo);

        TokenResponse response = jwtService.createToken(new JwtClaim(socialAccount.getUserId(), Role.USER));
        log.debug("oauthSignIn userId: {} tokenPair: {}", response.userId(), response.tokenPair());
        return response;
    }

    private SocialAccount getSocialAccount(OAuthUserInfo oAuthUserInfo) {
        return socialAccountRepository.findBySocialIdAndProvider(
                        oAuthUserInfo.socialId(),
                        Provider.KAKAO
                ).orElseGet(() -> createUser(oAuthUserInfo));
    }

    private SocialAccount createUser(OAuthUserInfo oAuthUserInfo) {
        Long userId = userService.createUser(oAuthUserInfo.nickname(), oAuthUserInfo.profileImageUrl());
        return socialAccountRepository.save(SocialAccount.create(userId, oAuthUserInfo));
    }

    public TokenResponse reissue(String refreshToken) {
        TokenResponse response = jwtService.reissue(refreshToken);
        log.debug("reissue userId: {} tokenPair: {}", response.userId(), response.tokenPair());
        return response;
    }

    public void signOut(Long userId, String refreshToken) {
        jwtService.signOut(userId, refreshToken);
    }

    public TokenResponse guestSignIn(String refreshToken) {
        TokenResponse response;
        if (Objects.isNull(refreshToken)) {
            User user = userService.createGuest();
            response = jwtService.createToken(new JwtClaim(user.getId(), user.getRole()));
        } else {
            response = jwtService.reissue(refreshToken);
        }
        log.debug("guestSignIn userId: {} tokenPair: {}", response.userId(), response.tokenPair());
        return response;
    }
}
