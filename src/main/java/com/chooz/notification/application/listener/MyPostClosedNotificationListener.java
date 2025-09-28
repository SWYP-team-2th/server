package com.chooz.notification.application.listener;

import com.chooz.notification.application.NotificationContentAssembler;
import com.chooz.notification.application.NotificationService;
import com.chooz.notification.application.dto.NotificationContent;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationType;
import com.chooz.post.application.dto.PostClosedNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MyPostClosedNotificationListener {

    private final NotificationService notificationService;
    private final NotificationContentAssembler notificationContentAssembler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMyPostClosed(PostClosedNotificationEvent postClosedNotificationEvent) {
        NotificationContent notificationContent = notificationContentAssembler.forMyPostClosed(
                postClosedNotificationEvent.postId(),
                postClosedNotificationEvent.receiverId()
        );
        Notification.create(
                NotificationType.MY_POST_CLOSED,
                postClosedNotificationEvent.eventAt(),
                notificationContent
        ).ifPresent(notificationService::create);
    }
}
