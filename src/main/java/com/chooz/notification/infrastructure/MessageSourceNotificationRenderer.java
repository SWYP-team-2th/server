package com.chooz.notification.infrastructure;

import com.chooz.common.util.Validator;
import com.chooz.notification.application.NotificationMessageRenderer;
import com.chooz.notification.application.dto.RenderedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageSourceNotificationRenderer implements NotificationMessageRenderer {

    private final MessageSource notificationMessageSource;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    @Override
    public RenderedMessage render(String code, Map<String, Object> vars) {
        String titleKey = code + ".title";
        String contentKey  = code + ".content";

        Object[] args = argsFrom(vars);
        String title = getMessage(titleKey, args);
        String content  = getMessage(contentKey , args);
        Validator.validateEmptyString(title);
        Validator.validateEmptyString(content);
        return new RenderedMessage(title, content);
    }

    private String getMessage(String key, Object[] args) {
        return notificationMessageSource.getMessage(key, args, DEFAULT_LOCALE);
    }

    private Object[] argsFrom(Map<String, Object> vars) {
        return new Object[] {
                vars.get("actorName"),
                vars.get("postTitle"),
        };
    }
}
