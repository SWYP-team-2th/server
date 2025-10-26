package com.chooz.auth.presentation.filter;

import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.UnauthorizedException;

import java.util.Objects;

public class HeaderTokenExtractor {

    public static final String BEARER = "Bearer ";

    public String extractToken(String authorization) {
        if (Objects.isNull(authorization) || !authorization.startsWith(BEARER)) {
            throw new UnauthorizedException(ErrorCode.INVALID_AUTH_HEADER);
        }
        return authorization.substring(BEARER.length());
    }
}
