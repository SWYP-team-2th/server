package com.chooz.notification.application.dto;

import com.chooz.notification.domain.Target;

import java.util.List;

public record NotificationContent (
        Long receiverId,
        Long actorId,
        String actorNickname,
        String actorProfileUrl,
        String imageUrl,
        List<Target> targets
){
}
