package com.swyp8team2.comment.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.common.dto.CursorDto;
import com.swyp8team2.user.domain.User;

import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        AuthorDto author,
        Long voteImageId,
        LocalDateTime createdAt,
        boolean isAuthor
) implements CursorDto {

    @Override
    @JsonIgnore
    public long getId() {
        return commentId;
    }

    public static CommentResponse of(Comment comment, User user, boolean isAuthor, Long voteImageId) {
        return new CommentResponse(comment.getId(),
                comment.getContent(),
                new AuthorDto(user.getId(), user.getNickname(), user.getProfileUrl()),
                voteImageId,
                comment.getCreatedAt(),
                isAuthor
        );
    }
}
