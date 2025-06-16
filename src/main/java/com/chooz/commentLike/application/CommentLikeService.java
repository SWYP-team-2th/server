package com.chooz.commentLike.application;

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
    public void createCommentLike(Long commentId, Long userId) {
        commentLikeCommandService.createCommentLike(commentId, userId);
    }

    @Transactional
    public void deleteCommentLike(Long commentId, Long userId) {
        commentLikeCommandService.deleteCommentLike(commentId, userId);
    }
}
