package com.chooz.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UpdatePostRequest(

        @NotNull
        String title,

        @NotNull
        String description,

        @Valid
        @NotNull
        CloseOptionDto closeOption,

        @Valid
        @NotNull
        PollOptionDto pollOption
) {
}
