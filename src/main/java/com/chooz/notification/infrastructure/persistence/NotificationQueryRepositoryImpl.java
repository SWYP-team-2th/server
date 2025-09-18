package com.chooz.notification.infrastructure.persistence;

import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Slice<Notification> findNotifications(Long userId, Long cursor, Pageable pageable) {
        return notificationJpaRepository.findByUserId(userId, cursor, pageable);
    }
}