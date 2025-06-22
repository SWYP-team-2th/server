package com.chooz.comment.presentation.dto;

import com.chooz.comment.domain.Comment;
import com.chooz.common.dto.CursorDto;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        boolean edited,
        LocalDateTime createdAt,
        CommentAuthorDto author,
        CommentLikeDto like
) implements CursorDto {

    public static CommentDto of (
            Comment comment,
            CommentAuthorDto author,
            CommentLikeDto like
    ){
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getEdited(),
                comment.getCreatedAt(),
                author,
                like
        );
    }

    @Override
    public long getId() {
        return this.id;
    }
}
