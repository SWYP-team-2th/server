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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostClosedNotificationListener {

    private final NotificationService notificationService;
    private final NotificationContentAssembler notificationContentAssembler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostClosed(PostClosedNotificationEvent postClosedNotificationEvent) {
        List<NotificationContent> notificationContents = notificationContentAssembler.forPostClosed(
                postClosedNotificationEvent.postId(),
                postClosedNotificationEvent.userId()
        );
        List<Notification> notifications = new ArrayList<>();
        notificationContents.forEach(notificationContent ->
                Notification.create(
                        NotificationType.POST_CLOSED,
                        postClosedNotificationEvent.eventAt(),
                        notificationContent
                ).ifPresent(notifications::add));
        notificationService.createAll(notifications);
    }
}
