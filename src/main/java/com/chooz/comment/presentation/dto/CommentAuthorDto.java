package com.chooz.comment.presentation.dto;

import com.chooz.user.domain.User;

public record CommentAuthorDto(
        long id,
        String nickname,
        String profileUrl
) {
    public static CommentAuthorDto of (User user) {
        return new CommentAuthorDto(user.getId(), user.getNickname(), user.getProfileUrl());
    }
}
