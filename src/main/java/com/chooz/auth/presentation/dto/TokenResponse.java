package com.chooz.auth.presentation.dto;

import com.chooz.auth.application.jwt.TokenPair;
import com.chooz.user.domain.Role;

public record TokenResponse(
        TokenPair tokenPair,
        Long userId,
        Role role
) { }
