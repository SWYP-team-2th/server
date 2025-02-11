package com.swyp8team2.auth.application;

import com.swyp8team2.auth.domain.OAuthUser;
import com.swyp8team2.auth.domain.SocialAccount;
import com.swyp8team2.auth.domain.SocialAccountRepository;
import com.swyp8team2.auth.domain.Provider;
import com.swyp8team2.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Provider provider = getProvider(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthUserInfo oAuthUserInfo = OAuthUserInfo.of(provider, attributes);

        SocialAccount socialAccount = socialAccountRepository.findBySocialIdAndProvider(
                        oAuthUserInfo.socialId(), provider)
                .orElseGet(() -> createUser(oAuthUserInfo, provider));

        return new OAuthUser(oAuth2User.getAuthorities(), attributes, userNameAttributeName, socialAccount.getUserId());
    }

    private Provider getProvider(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return Provider.of(registrationId);
    }

    private SocialAccount createUser(OAuthUserInfo oAuthUserInfo, Provider provider) {
        String email = oAuthUserInfo.email();
        Long userId = userService.createUser(email);
        return socialAccountRepository.save(SocialAccount.create(userId, oAuthUserInfo.socialId(), provider, email));
    }
}
