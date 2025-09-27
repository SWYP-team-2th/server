package com.chooz.notification.application.service;

import com.chooz.notification.application.NotificationService;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification create(Notification notification) {
        return notificationService.existsByDedupKey(notification.getReceiverId(), notification.getDedupKey())
                ? null
                : notificationRepository.save(notification);
    }
}
