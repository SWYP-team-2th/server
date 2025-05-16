package com.chooz.auth.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record OAuthSignInRequest(
        @NotNull
        String code,

        @NotNull
        String redirectUri
) {
}
