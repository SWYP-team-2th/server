package com.chooz.post.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PollChoiceRequestDto(
        @Size(min = 1, max = 50)
        String title,

        @NotEmpty
        String imageUrl
) {
}
