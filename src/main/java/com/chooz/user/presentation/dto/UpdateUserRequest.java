package com.chooz.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank
        String nickname,

        String profileImageUrl
) {}

