package com.chooz.comment.application;

import com.chooz.comment.presentation.dto.CommentIdResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    public CursorBasePaginatedResponse<CommentResponse> findComments(Long postId, Long userId, Long cursorId, int size) {
        return commentQueryService.findComments(postId, userId, cursorId, size);
    }

    @Transactional
    public CommentIdResponse createComment(Long postId, CommentRequest commentRequest, Long userId) {
        return commentCommandService.createComment(postId, commentRequest, userId);
    }

    @Transactional
    public CommentIdResponse updateComment(Long postId, Long commentId, CommentRequest commentRequest, Long userId) {
        return commentCommandService.updateComment(postId, commentId, commentRequest, userId);
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        commentCommandService.deleteComment(postId, commentId, userId);
    }
}
