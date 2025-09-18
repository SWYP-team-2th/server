package com.chooz.notification.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface NotificationQueryRepository {
    Slice<Notification> findNotifications(Long userId, Long cursor, Pageable pageable);
}
