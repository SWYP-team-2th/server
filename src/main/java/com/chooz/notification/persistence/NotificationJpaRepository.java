package com.chooz.notification.persistence;

import com.chooz.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    boolean existsByReceiverIdAndIsReadFalseAndDeletedFalse(Long userId);
}
