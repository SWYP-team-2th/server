package com.chooz.user.presentation.dto;

import com.chooz.user.domain.User;

public record UserInfoResponse(
        Long id,
        String nickname,
        String profileUrl,
        boolean is_onboard,
        boolean notification
) {
    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl(),
                user.is_onboard(),
                user.isNotification()
        );
    }
}
