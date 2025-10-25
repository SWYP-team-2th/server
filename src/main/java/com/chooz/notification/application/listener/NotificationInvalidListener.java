package com.chooz.notification.application.listener;

import com.chooz.common.event.DeleteEvent;
import com.chooz.notification.application.NotificationService;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationInvalidListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void inValid(DeleteEvent deleteEvent) {
        notificationService.findByTargetIdAndType(deleteEvent.id(), TargetType.valueOf(deleteEvent.domain()))
                .forEach(Notification::invalidate);
    }
}
