package com.chooz.post.presentation.dto;

public record PollChoiceVoteResponse(
        Long id,
        String title,
        String imageUrl,
        Long voteId
) {
}
