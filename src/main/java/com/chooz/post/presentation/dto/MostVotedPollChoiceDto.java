package com.chooz.post.presentation.dto;

import com.chooz.post.application.dto.PollChoiceVoteInfo;

public record MostVotedPollChoiceDto(
        long id,
        String title,
        long voteCount,
        String voteRatio
) {
    public static MostVotedPollChoiceDto of(PollChoiceVoteInfo pollChoiceVoteInfo, String voteRatio) {
        return new MostVotedPollChoiceDto(
                pollChoiceVoteInfo.postId(),
                pollChoiceVoteInfo.title(),
                pollChoiceVoteInfo.voteCounts(),
                voteRatio
        );
    }
}