package com.chooz.support.fixture;

import com.chooz.comment.domain.CommentLike;

public class CommentLikeFixture {

    public static CommentLike createDefaultCommentLike(Long commentId) {
        return createCommentLikeBuilder()
                .commentId(commentId)
                .build();
    }
    public static CommentLike.CommentLikeBuilder createCommentLikeBuilder() {
        return CommentLike.builder()
                .commentId(1L)
                .userId(1L);
    }
}
