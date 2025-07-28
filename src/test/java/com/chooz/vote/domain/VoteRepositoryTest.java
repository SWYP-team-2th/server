package com.chooz.vote.domain;

import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.support.RepositoryTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class VoteRepositoryTest extends RepositoryTest {

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("단일 투표 참여자 수 조회")
    void countVoterByPostId_single() throws Exception {
        //given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user1.getId()));
        int voterCount = 5;
        for (int i = 0; i < voterCount; i++) {
            User user = userRepository.save(UserFixture.createDefaultUser());
            voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        }

        //when
        long res = voteRepository.countVoterByPostId(post.getId());

        //then
        assertThat(res).isEqualTo(voterCount);
    }

    @Test
    @DisplayName("단일 투표 참여자 수 조회")
    void countVoterByPostId_multiple() throws Exception {
        //given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user1.getId())
                .pollOption(
                        PostFixture.createPollOptionBuilder()
                                .pollType(PollType.MULTIPLE)
                                .build())
                .build());
        int voterCount = 5;
        for (int i = 0; i < voterCount; i++) {
            User user = userRepository.save(UserFixture.createDefaultUser());
            voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
            voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(1).getId()));
        }

        //when
        long res = voteRepository.countVoterByPostId(post.getId());

        //then
        assertThat(res).isEqualTo(voterCount);
    }
}
