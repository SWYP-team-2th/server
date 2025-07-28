package com.chooz.common.dev;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInitConfig {

    private final DataInitializer dataInitializer;

    @PostConstruct
    public void init() {
        dataInitializer.init();
    }
}
