package com.chooz.notification.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Target;
import com.chooz.notification.domain.TargetType;

import java.time.LocalDateTime;

public record NotificationResponse (
        Long id,
        Long userId,
        Long actorId,
        NotificationType type,
        Target target,
        String title,
        String body,
        String thumbUrl,
        String profileImageUrl,
        boolean isRead,
        LocalDateTime eventAt
)implements CursorDto{

    public static NotificationResponse of (Notification notification){
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getActorId(),
                notification.getType(),
                notification.getTarget(),
                notification.getTitle(),
                notification.getBody(),
                notification.getThumbUrl(),
                notification.getProfileImageUrl(),
                notification.isRead(),
                notification.getEventAt()
        );
    }

    @Override
    public long getId() { return this.id; }
}
