package com.chooz.notification.application.service;

import com.chooz.notification.application.NotificationService;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification create(Notification notification) {
        return notificationQueryRepository.existsByDedupKey(notification.getReceiverId(), notification.getDedupKey())
                ? null
                : notificationRepository.save(notification);
    }
}
