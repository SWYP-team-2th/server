package com.chooz.vote.presentation.dto;

public record PostImageVoteStatusResponse(
        Long id,
        String imageName,
        int voteCount,
        String voteRatio
) {
}
