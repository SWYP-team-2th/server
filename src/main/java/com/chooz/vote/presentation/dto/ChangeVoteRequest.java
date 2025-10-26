package com.chooz.vote.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeVoteRequest(
        @NotNull
        Long imageId
) {
}
