package com.chooz.notification.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.notification.application.dto.NotificationDto;
import com.chooz.notification.domain.Actor;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.Receiver;
import com.chooz.notification.domain.Target;

import java.time.LocalDateTime;

public record NotificationResponse (
        Long id,
        Long postId,
        Receiver receiver,
        Actor actor,
        Target target,
        boolean isValid,
        boolean isRead,
        LocalDateTime eventAt
)implements CursorDto{

    public static NotificationResponse of (NotificationDto notificationDto){
        return new NotificationResponse(
                notificationDto.id(),
                notificationDto.postId(),
                new Receiver(notificationDto.receiverId(), notificationDto.receiverNickname()),
                new Actor(
                        notificationDto.actorId(),
                        notificationDto.actorNickname(),
                        notificationDto.actorProfileUrl()
                ),
                new Target(
                        notificationDto.targetId(),
                        notificationDto.targetType(),
                        notificationDto.targetImageUrl()
                ),
                notificationDto.isValid(),
                notificationDto.isRead(),
                notificationDto.eventAt()
        );
    }

    @Override
    public long getId() { return this.id; }
}
