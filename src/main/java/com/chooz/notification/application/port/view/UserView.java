package com.chooz.notification.application.port.view;

public record UserView(
        Long id,
        String nickname,
        String profileUrl
) {}
