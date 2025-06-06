package com.chooz.comment.presentation.dto;

public record CommentAnchorResponse(
        Long commentId,
        String content,
        String anchor
){}
