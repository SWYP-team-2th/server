package com.chooz.post.application;

import com.chooz.post.domain.*;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.vote.application.VotedEvent;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostVotedEventListenerTest extends IntegrationTest {

    @Autowired
    PostVotedEventListener postVotedEventListener;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    @DisplayName("CloseType이 VOTER이고 최대 투표자 수에 도달하면 게시글이 마감된다")
    void handle_closeTypeVoterAndReachMaxVoterCount() {
        // given
        Long userId = 1L;
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .closeOption(
                                PostFixture.createCloseOptionBuilder()
                                        .closeType(CloseType.VOTER)
                                        .maxVoterCount(1)
                                        .build()
                        )
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();
        voteRepository.save(VoteFixture.createDefaultVote(userId, post.getId(), pollChoiceId));

        // when
        postVotedEventListener.handle(new VotedEvent(post.getId(), List.of(pollChoiceId), userId));

        // then
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    @DisplayName("CloseType이 VOTER이지만 최대 투표자 수에 도달하지 않으면 게시글이 마감되지 않는다")
    void handle_closeTypeVoterButNotReachMaxVoterCount() {
        // given
        Long userId = 1L;
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .closeOption(
                                PostFixture.createCloseOptionBuilder()
                                        .closeType(CloseType.VOTER)
                                        .maxVoterCount(5)
                                        .build()
                        )
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();
        voteRepository.save(VoteFixture.createDefaultVote(userId, post.getId(), pollChoiceId));

        // when
        postVotedEventListener.handle(new VotedEvent(post.getId(), List.of(1L), userId));

        // then
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(updatedPost.getStatus()).isEqualTo(Status.PROGRESS);
    }
}
