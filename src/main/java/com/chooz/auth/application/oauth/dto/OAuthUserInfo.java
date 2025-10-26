package com.chooz.auth.application.oauth.dto;

import com.chooz.auth.domain.Provider;

public record OAuthUserInfo(
        String socialId,
        String profileImageUrl,
        String nickname,
        Provider provider
) {
}
