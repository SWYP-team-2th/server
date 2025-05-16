package com.chooz.common.exception;

import lombok.Builder;

import java.util.List;

@Builder
public record DiscordMessage(
        String content,
        List<Embed> embeds
) {

    @Builder
    record Embed(
            String title,
            String description
    ) { }
}
