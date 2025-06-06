package com.chooz.comment.support;

import com.chooz.comment.domain.Comment;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {
    public void validateCommentOwnership(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
        }
    }

    public void validateCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new BadRequestException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    public void validateCommentAccess(Comment comment, Long postId, Long userId) {
        validateCommentBelongsToPost(comment, postId);
        validateCommentOwnership(comment, userId);
    }

}
