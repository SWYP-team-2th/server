package com.chooz.vote.presentation.dto;

public record PollChoiceStatusResponse(
        Long id,
        String title,
        int voteCount,
        String voteRatio
) {
}
