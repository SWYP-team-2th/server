package com.chooz.notification.application;

import com.chooz.notification.application.dto.CommentLikedContent;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.TargetType;
import com.chooz.notification.domain.event.CommentLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CommentLikeNotificationListener {

    private final NotificationCommandService notificationCommandService;
    private final NotificationContentAssembler notificationContentAssembler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentLiked(CommentLikedEvent e) {
        CommentLikedContent commentLikedContent = notificationContentAssembler.forCommentLiked(e.commentId(), e.likerId());
         Notification.create(
                 commentLikedContent.getCommentAuthorId(),
                 commentLikedContent.getCommentAuthorName(),
                 e.likerId(),
                 commentLikedContent.getActorName(),
                 commentLikedContent.getActorProfileImageUrl(),
                 e.commentId(),
                 TargetType.COMMENT,
                 commentLikedContent.getTargetThumbnailUrl(),
                 e.eventAt()
        ).ifPresent(notificationCommandService::create);
    }
}
