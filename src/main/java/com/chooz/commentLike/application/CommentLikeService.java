package com.chooz.commentLike.application;

import com.chooz.commentLike.presentation.dto.CommentLikeIdResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentLikeService {

    private final CommentLikeCommandService commentLikeCommandService;

    @Transactional
    public CommentLikeIdResponse createCommentLike(Long commentId, Long userId) {
        return commentLikeCommandService.createCommentLike(commentId, userId);
    }

    @Transactional
    public void deleteCommentLike(Long commentLikeId, Long userId) {
        commentLikeCommandService.deleteCommentLike(commentLikeId, userId);
    }
}
