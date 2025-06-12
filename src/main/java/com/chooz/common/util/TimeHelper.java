package com.chooz.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class TimeHelper {

    private static final String ASIA_SEOUL = "Asia/Seoul";

    private final Clock clock;

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public long nowMillis() {
        return now().atZone(ZoneId.of(ASIA_SEOUL))
                .toInstant()
                .toEpochMilli();
    }
}
