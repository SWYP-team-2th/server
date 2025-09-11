package com.chooz.common.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@ConfigurationPropertiesScan(basePackages = "com.chooz")
public class CommonConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
