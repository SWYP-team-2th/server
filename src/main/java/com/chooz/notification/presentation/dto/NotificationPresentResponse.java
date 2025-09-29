package com.chooz.notification.presentation.dto;

public record NotificationPresentResponse(boolean present){
    public static NotificationPresentResponse of(boolean present) {
        return new NotificationPresentResponse(present);
    }
}
