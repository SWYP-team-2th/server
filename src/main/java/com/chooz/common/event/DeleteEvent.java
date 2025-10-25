package com.chooz.common.event;

public record DeleteEvent(Long id, String domain) {
    public static DeleteEvent of(Long id, String domain) {
        return new DeleteEvent(id, domain);
    }
}
