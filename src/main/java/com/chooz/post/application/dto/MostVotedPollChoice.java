package com.chooz.post.application.dto;

public record MostVotedPollChoice(
        Long postId,
        Long pollChoiceId,
        String title,
        Long voteCount
) {
}
