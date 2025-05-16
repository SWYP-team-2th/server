package com.chooz.auth.domain;

import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Provider {

    KAKAO("kakao");

    private final String registrationId;

    public static Provider of(String registrationId) {
        return Arrays.stream(Provider.values())
                .filter(provider -> provider.registrationId.equals(registrationId))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.INVALID_INPUT_VALUE));
    }
}
