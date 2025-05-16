package com.chooz.vote.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull
        Long imageId
) {
}
