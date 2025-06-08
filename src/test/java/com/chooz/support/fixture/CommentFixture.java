package com.chooz.support.fixture;

import com.chooz.comment.domain.Comment;

public class CommentFixture {

    public static Comment createDefaultComment(Long userId, Long postId) {
        return Comment.create(postId, userId, "content");
    }
    public static Comment createWithContentComment(Long userId, Long postId, String content) {
        return Comment.create(userId, postId, content);
    }

    public static Comment.CommentBuilder createCommentBuilder() {
        return Comment.builder()
                .postId(1L)
                .userId(1L)
                .content("This is a comment")
                .edited(false);
    }
}
