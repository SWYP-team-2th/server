package com.chooz.auth.application.jwt;

import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;

import java.util.Objects;

public record TokenPair(
        String accessToken,
        String refreshToken
) {

    public TokenPair {
        if (Objects.isNull(accessToken) || Objects.isNull(refreshToken)) {
            throw new InternalServerException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
