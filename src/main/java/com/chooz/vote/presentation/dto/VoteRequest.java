package com.chooz.vote.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VoteRequest(
        @NotNull
        Long postId,

        @NotNull
        List<Long> pollChoiceIds
) {
}
