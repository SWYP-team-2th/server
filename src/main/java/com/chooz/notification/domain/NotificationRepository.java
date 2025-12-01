package com.chooz.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    void saveAll(List<Notification> notifications);
    Optional<Notification> findNotificationById(Long id);
    boolean existsByReceiverIdAndIsReadFalseAndDeletedFalse(Long userId);
    List<Notification> findByTargetIdAndType(Long targetId, TargetType targetType);

    void deleteAllByUserId(Long userId);
}
