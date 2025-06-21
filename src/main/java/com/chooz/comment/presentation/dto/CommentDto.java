package com.chooz.comment.presentation.dto;

import com.chooz.comment.domain.Comment;
import com.chooz.common.dto.CursorDto;

import java.time.LocalDateTime;

public record CommentDto(
        long id,
        String content,
        boolean edited,
        LocalDateTime createdAt,
        CommentAuthorDto commentAuthorDto,
        CommentLikeDto commentLikeDto
) implements CursorDto {

    public static CommentDto of (
            Comment comment,
            CommentAuthorDto commentAuthorDto,
            CommentLikeDto commentLikeDto
    ){
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getEdited(),
                comment.getCreatedAt(),
                commentAuthorDto,
                commentLikeDto
        );
    }

    @Override
    public long getId() {
        return this.id;
    }
}
