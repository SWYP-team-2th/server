package com.chooz.comment.presentation.dto;

public interface CommentLikeCountProjection {
    Long getCommentId();
    Long getLikeCount();
}
