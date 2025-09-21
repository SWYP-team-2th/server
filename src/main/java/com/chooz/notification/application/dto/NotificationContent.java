package com.chooz.notification.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class NotificationContent {
    private final String actorName;
    private final String actorProfileImageUrl;
    private final String targetThumbnailUrl;

}
