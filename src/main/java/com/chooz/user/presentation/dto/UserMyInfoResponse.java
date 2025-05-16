package com.chooz.user.presentation.dto;

import com.chooz.user.domain.Role;
import com.chooz.user.domain.User;

public record UserMyInfoResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        Role role
) {
    public static UserMyInfoResponse of(User user) {
        return new UserMyInfoResponse(user.getId(), user.getNickname(), user.getProfileUrl(), user.getRole());
    }
}
