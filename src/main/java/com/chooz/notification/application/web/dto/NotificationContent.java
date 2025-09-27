package com.chooz.notification.application.web.dto;

import com.chooz.notification.domain.Target;
import java.util.List;

public record NotificationContent (
        Long receiverId,
        Long actorId,
        String title,
        String content,
        String profileUrl,
        String imageUrl,
        List<Target> targets
){
    public static NotificationContent of(
            Long receiverId,
            Long actorId,
            String title,
            String content,
            String profileUrl,
            String imageUrl,
            List<Target> targets
            ) {
        return new NotificationContent(
                receiverId,
                actorId,
                title,
                content,
                profileUrl,
                imageUrl,
                targets
        );
    }
}
