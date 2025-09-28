package com.chooz.commentLike.application;

import com.chooz.commentLike.domain.CommentLike;
import com.chooz.commentLike.domain.CommentLikeRepository;
import com.chooz.commentLike.presentation.dto.CommentLikeIdResponse;
import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.commentLike.domain.event.CommentLikedNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentLikeCommandService {

    private final CommentLikeRepository commentLikeRepository;
    private final EventPublisher eventPublisher;

    public CommentLikeIdResponse createCommentLike(Long commentId, Long userId) {
        if(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)){
            throw new BadRequestException(ErrorCode.COMMENT_LIKE_NOT_FOUND);
        }
        CommentLike commentLike = commentLikeRepository.save(CommentLike.create(commentId, userId));

        eventPublisher.publish(new CommentLikedNotificationEvent(
                commentId,
                commentLike.getId(),
                userId,
                LocalDateTime.now()
                ));
        return new CommentLikeIdResponse(
                commentLike.getId(),
                commentLikeRepository.countByCommentId(commentId)
        );
    }

    public CommentLikeIdResponse deleteCommentLike(Long commentId, Long commentLikeId, Long userId) {
        CommentLike commentLike = commentLikeRepository.findById(commentLikeId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_LIKE_NOT_FOUND));
        if(!commentLike.getUserId().equals(userId)){
            throw new BadRequestException(ErrorCode.NOT_COMMENT_LIKE_AUTHOR);
        }
        commentLikeRepository.delete(commentLike);
        return new CommentLikeIdResponse(
                null,
                commentLikeRepository.countByCommentId(commentId)
        );
    }

    public void deleteCommentLikeByCommentId(Long commentId) {
        commentLikeRepository.deleteByCommentId(commentId);
    }
}
