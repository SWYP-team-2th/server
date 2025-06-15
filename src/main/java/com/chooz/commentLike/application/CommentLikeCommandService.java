package com.chooz.commentLike.application;

import com.chooz.commentLike.domain.CommentLike;
import com.chooz.commentLike.domain.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentLikeCommandService {

    private final CommentLikeRepository commentLikeRepository;

    public void createLikeComment(Long commentId, Long userId) {
        boolean alreadyLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        if (alreadyLiked) {
            return;
        }
        commentLikeRepository.save(CommentLike.create(commentId, userId));
    }

    public void deleteLikeComment(Long commentId, Long userId) {
        commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(commentLikeRepository::delete);
    }
}
