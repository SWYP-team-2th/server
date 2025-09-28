package com.chooz.notification.domain;

import com.chooz.notification.presentation.dto.NotificationPresentResponse;

import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findNotificationById(Long id);
    boolean existsByReceiverIdAndIsReadFalseAndDeletedFalse(Long userId);
}
