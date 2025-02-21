package com.swyp8team2.common.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@ConfigurationPropertiesScan
public class CommonConfig {


    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
