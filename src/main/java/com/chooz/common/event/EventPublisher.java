package com.chooz.common.event;

public interface EventPublisher {
    <T> void publish(T event);
}
