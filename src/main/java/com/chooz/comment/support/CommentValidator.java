package com.chooz.comment.support;

import com.chooz.comment.domain.Comment;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CommentActive;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {
    public void validateCommentOwnership(Comment comment, Long userId) {
        if (!comment.getUserId().equals(userId)) {
            throw new BadRequestException(ErrorCode.NOT_COMMENT_AUTHOR);
        }
    }

    public void validateCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPostId().equals(postId)) {
            throw new BadRequestException(ErrorCode.COMMENT_NOT_BELONG_TO_POST);
        }
    }

    public void validateCommentAccess(Comment comment, Long postId, Long userId) {
        validateCommentBelongsToPost(comment, postId);
        validateCommentOwnership(comment, userId);
    }
    public void validateCommentActive(CommentActive commentActive) {
        if(commentActive.equals(CommentActive.CLOSED)) {
            throw new BadRequestException(ErrorCode.CLOSE_COMMENT_ACTIVE);
        }
    }
    public void validateContentLength(String content){
        if(content.length() > 200) {
            throw new BadRequestException(ErrorCode.COMMENT_LENGTH_OVER);
        }
    }


}
