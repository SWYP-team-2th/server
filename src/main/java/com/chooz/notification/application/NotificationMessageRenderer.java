package com.chooz.notification.application;

import com.chooz.notification.application.dto.RenderedMessage;

import java.util.Map;

public interface NotificationMessageRenderer {
    RenderedMessage render(String type, Map<String, Object> vars);
}
