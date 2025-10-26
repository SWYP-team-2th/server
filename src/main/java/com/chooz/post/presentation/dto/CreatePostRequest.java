package com.chooz.post.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePostRequest(

        @NotBlank
        String title,

        @NotNull
        String description,

        @Valid
        @NotNull
        List<PollChoiceRequestDto> pollChoices,

        @Valid
        @NotNull
        PollOptionDto pollOption,

        @Valid
        @NotNull
        CloseOptionDto closeOption
) {
}
