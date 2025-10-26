package com.chooz.post.application.dto;

public record PollChoiceVoteInfo(
        Long postId,
        Long pollChoiceId,
        Long voteCounts,
        String title
) {
}
