package com.chooz.vote.presentation.dto;

public record VoteStatusResponse(
        Long id,
        String title,
        String imageUrl,
        long voteCount,
        String voteRatio
) {
}
