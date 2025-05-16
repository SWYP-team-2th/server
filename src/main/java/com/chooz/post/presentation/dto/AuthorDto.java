package com.chooz.post.presentation.dto;

import com.chooz.user.domain.User;

public record AuthorDto(
        Long id,
        String nickname,
        String profileUrl
) {
    public static AuthorDto of(User user) {
        return new AuthorDto(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl()
        );
    }
}
