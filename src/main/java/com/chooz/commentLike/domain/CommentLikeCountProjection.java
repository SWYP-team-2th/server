package com.chooz.commentLike.domain;

public interface CommentLikeCountProjection {
    Long getCommentId();
    Long getLikeCount();
}
