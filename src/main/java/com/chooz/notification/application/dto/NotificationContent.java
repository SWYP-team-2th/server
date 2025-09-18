package com.chooz.notification.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class NotificationContent {
    private final String title;
    private final String body;
    private final String thumbnailUrl;
    private final String profileImageUrl;
}
