package com.chooz.common.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "com.chooz")
public class CommonConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @Bean
    public Clock clock() {
        System.out.println("LocalDateTime.now() = " + LocalDateTime.now());
        return Clock.systemDefaultZone();
    }

}
