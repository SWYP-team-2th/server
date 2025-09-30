package com.chooz.notification.application.listener;

import com.chooz.notification.application.NotificationContentAssembler;
import com.chooz.notification.application.NotificationService;
import com.chooz.notification.application.dto.NotificationContent;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationType;
import com.chooz.vote.application.VotedNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class VotedNotificationListener {

    private final NotificationService notificationService;
    private final NotificationContentAssembler notificationContentAssembler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVoted(VotedNotificationEvent votedNotificationEvent) {
        NotificationContent notificationContent = notificationContentAssembler.forVoted(
                votedNotificationEvent.postId(),
                votedNotificationEvent.voterId()
        );
        Notification.create(
                NotificationType.POST_VOTED,
                votedNotificationEvent.eventAt(),
                notificationContent
        ).ifPresent(notificationService::create);
    }
}
