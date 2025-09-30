package com.chooz.notification.persistence;

import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public void saveAll(List<Notification> notifications) {
        notificationJpaRepository.saveAll(notifications);
    }

    @Override
    public Optional<Notification> findNotificationById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public boolean existsByReceiverIdAndIsReadFalseAndDeletedFalse(Long userId) {
        return notificationJpaRepository.existsByReceiverIdAndIsReadFalseAndDeletedFalse(userId);
    }

}