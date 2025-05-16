package com.chooz.comment.presentation.dto;

public record AuthorDto(
        Long userId,
        String nickname,
        String profileUrl
) {
}
