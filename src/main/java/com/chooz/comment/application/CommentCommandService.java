package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentIdResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.comment.support.CommentValidator;
import com.chooz.commentLike.application.CommentLikeCommandService;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.PostRepository;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentCommandService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentValidator commentValidator;
    private final CommentLikeCommandService commentLikeCommandService;

    public CommentIdResponse createComment(Long postId, CommentRequest commentRequest, Long userId) {
        commentValidator.validateContentLength(commentRequest.content());
        Comment commentForSave = Comment.create(
                postRepository.findById(postId)
                        .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND)).getId(),
                userRepository.findById(userId)
                        .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND)).getId(),
                commentRequest.content()
        );
        Comment commentFromSave = commentRepository.save(commentForSave);
        return new CommentIdResponse(commentFromSave.getId());
    }

    public CommentIdResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest, Long userId) {
        commentValidator.validateContentLength(commentRequest.content());
        Comment commentForUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));
        commentValidator.validateCommentAccess(commentForUpdate, postId, userId);
        commentForUpdate.updateComment(commentRequest.content());
        return new CommentIdResponse(commentForUpdate.getId());
    }

    public void deleteComment(Long postId, Long commentId, Long userId) {
        commentLikeCommandService.deleteCommentLikeByCommentId(commentId);
        Comment commentForDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));
        commentValidator.validateCommentAccess(commentForDelete, postId, userId);
        commentRepository.delete(commentForDelete);
    }
}
