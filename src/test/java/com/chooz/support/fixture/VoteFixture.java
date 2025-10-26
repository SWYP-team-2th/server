package com.chooz.support.fixture;

import com.chooz.vote.domain.Vote;

public class VoteFixture {

    public static Vote createDefaultVote(Long userId, Long postId, Long pollChoiceId) {
        return Vote.create(userId, postId, pollChoiceId);
    }

    public static Vote.VoteBuilder createVoteBuilder() {
        return Vote.builder()
                .postId(1L)
                .pollChoiceId(1L)
                .userId(1L);
    }
}
