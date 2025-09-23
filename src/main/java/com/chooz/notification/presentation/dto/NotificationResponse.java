package com.chooz.notification.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.notification.application.dto.NotificationDto;
import com.chooz.notification.domain.Actor;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Receiver;
import com.chooz.notification.domain.Target;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationResponse (
        Long id,
        Long receiverId,
        Actor actor,
        NotificationType notificationType,
        List<Target> targets,
        String imageUrl,
        boolean isValid,
        boolean isRead,
        LocalDateTime eventAt
)implements CursorDto{
    public static NotificationResponse of (NotificationDto notificationDto){
        return new NotificationResponse(
                notificationDto.notificationRowDto().id(),
                notificationDto.notificationRowDto().receiverId(),
                new Actor(
                        notificationDto.notificationRowDto().actorId(),
                        notificationDto.notificationRowDto().actorNickname(),
                        notificationDto.notificationRowDto().actorProfileUrl()
                ),
                notificationDto.notificationRowDto().notificationType(),
                List.copyOf(notificationDto.targets().stream().map(t -> Target.of(t.id(), t.type())).toList()),
                notificationDto.notificationRowDto().imageUrl(),
                notificationDto.notificationRowDto().isValid(),
                notificationDto.notificationRowDto().isRead(),
                notificationDto.notificationRowDto().eventAt()
        );
    }
    @Override
    public long getId() { return this.id; }
}
