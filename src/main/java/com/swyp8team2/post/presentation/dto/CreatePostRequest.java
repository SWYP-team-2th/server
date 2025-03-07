package com.swyp8team2.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePostRequest(
        @NotNull
        String description,

        @Valid @NotNull
        List<PostImageRequestDto> images
) {
}
