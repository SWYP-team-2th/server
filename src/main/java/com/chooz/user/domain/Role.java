package com.chooz.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("낫또"), USER("뽀또");

    private final String nickname;
}
