package com.chooz.notification.domain;

import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
}
