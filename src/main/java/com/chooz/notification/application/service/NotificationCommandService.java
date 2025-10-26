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
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAll(List<Notification> notifications) {
        List<Notification> existsNotifications = notificationQueryRepository.findNotificationsByDedupKey(notifications);
        Set<String> existingPairs = getExistingPairs(existsNotifications);
        List<Notification> toSave = getNotificationsNotDuplicated(notifications, existingPairs);
        if (!toSave.isEmpty()) {
            notificationRepository.saveAll(toSave);
        }
    }
    private Set<String> getExistingPairs(List<Notification> existsNotifications) {
        return existsNotifications.stream()
                .map(n -> n.getReceiverId() + "|" + n.getDedupKey())
                .collect(Collectors.toSet());
    }
    private List<Notification> getNotificationsNotDuplicated(List<Notification> notifications, Set<String> existingPairs) {
        return notifications.stream()
                .filter(n -> !existingPairs.contains(n.getReceiverId() + "|" + n.getDedupKey()))
                .toList();
    }
    @Transactional
    public void markRead(Long notificationId){
        Notification notification = notificationRepository.findNotificationById(notificationId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.markRead();
    }
}
