package com.chooz.user.presentation.dto;

import com.chooz.user.domain.User;

public record UserInfoResponse(
        Long id,
        String nickname,
        String profileUrl
) {
    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileUrl());
    }
}
