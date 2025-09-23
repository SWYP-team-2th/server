package com.chooz.notification.application;

import com.chooz.notification.application.dto.NotificationContent;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationRepository;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.TargetType;
import com.chooz.notification.domain.event.CommentLikedNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CommentLikeNotificationListener {

    private final NotificationCommandService notificationCommandService;
    private final NotificationContentAssembler notificationContentAssembler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentLiked(CommentLikedNotificationEvent commentLikedNotificationEvent) {
        NotificationContent notificationContent = notificationContentAssembler.forCommentLiked(
                commentLikedNotificationEvent.commentId(),
                commentLikedNotificationEvent.likerId()
        );
         Notification.create(
                 NotificationType.COMMENT_LIKED,
                 commentLikedNotificationEvent.eventAt(),
                 notificationContent
        ).ifPresent(notificationCommandService::create);
    }
}
