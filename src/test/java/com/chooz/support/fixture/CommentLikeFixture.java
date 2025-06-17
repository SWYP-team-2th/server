package com.chooz.support.fixture;

import com.chooz.commentLike.domain.CommentLike;

public class CommentLikeFixture {

    public static CommentLike createDefaultCommentLike(Long userId, Long commentId) {
        return createCommentLikeBuilder()
                .userId(userId)
                .commentId(commentId)
                .build();
    }
    public static CommentLike.CommentLikeBuilder createCommentLikeBuilder() {
        return CommentLike.builder()
                .commentId(1L)
                .userId(1L);
    }
}
