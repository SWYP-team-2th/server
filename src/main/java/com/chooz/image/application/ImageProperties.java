package com.chooz.image.application;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "image")
public record ImageProperties(
        @NotBlank String path,
        @NotBlank String endpoint
) {
}