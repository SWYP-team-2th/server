package com.chooz.notification.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Target;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationPresentResponse(boolean present){
    public static NotificationPresentResponse of(boolean present) {
        return new NotificationPresentResponse(present);
    }
}
