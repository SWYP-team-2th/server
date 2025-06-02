package com.chooz.post.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class ShareUrlKeyGenerator {
    
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Clock clock;
    
    public String generateKey() {
        int currentCount = counter.getAndUpdate(i -> i > 100 ? i + 1 : 0);
        long now = LocalDateTime.now(clock)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        return String.format("%d%d", now, currentCount);
    }
}
