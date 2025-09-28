package com.chooz.notification.application.service;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional
    public void markRead(Long notificationId){
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.markRead();
    }
}
