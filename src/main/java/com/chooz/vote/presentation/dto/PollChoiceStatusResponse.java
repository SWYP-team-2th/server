package com.chooz.vote.presentation.dto;

public record PollChoiceStatusResponse(
        Long id,
        String imageName,
        int voteCount,
        String voteRatio
) {
}
