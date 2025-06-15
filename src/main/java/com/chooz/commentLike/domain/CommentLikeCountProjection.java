package com.chooz.comment.domain;

public interface CommentLikeCountProjection {
    Long getCommentId();
    Long getLikeCount();
}
