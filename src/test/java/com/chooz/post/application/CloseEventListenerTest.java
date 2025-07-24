package com.chooz.post.application;

import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.domain.Status;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.application.VotedEvent;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CloseEventListenerTest extends IntegrationTest {

    @Autowired
    PostVotedEventListener postVotedEventListener;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    @DisplayName("참여자 수로 마감")
    void handle() throws Exception {
        //given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user1.getId())
                .closeOption(
                        PostFixture.createCloseOptionBuilder()
                                .closeType(CloseType.VOTER)
                                .maxVoterCount(5)
                                .build())
                .build());
        int voterCount = 5;
        for (int i = 0; i < voterCount; i++) {
            User user = userRepository.save(UserFixture.createDefaultUser());
            voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        }

        //when
        postVotedEventListener.handle(new VotedEvent(post.getId(), List.of(post.getPollChoices().get(0).getId()), user1.getId()));

        //then
        Post findPost = postRepository.findById(post.getId()).get();
        assertThat(findPost.getStatus()).isEqualTo(Status.CLOSED);
    }
}
