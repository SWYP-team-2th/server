package com.chooz.notification.infrastructure;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Bean(name = "messageSource")
    public MessageSource notificationMessageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames("classpath:notification/messages");
        ms.setDefaultEncoding("UTF-8");
        return ms;
    }
}
