package com.chooz.vote.presentation.dto;

public record VoteResultResponse(
        Long id,
        String title,
        String imageUrl,
        long voteCount,
        String voteRatio
) {
}
