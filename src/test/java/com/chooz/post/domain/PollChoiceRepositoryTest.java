package com.chooz.post.domain;

import com.chooz.post.application.dto.PollChoiceVoteInfo;
import com.chooz.support.RepositoryTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class PollChoiceRepositoryTest extends RepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PollChoiceRepository pollChoiceRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    void findPollChoiceWithVoteInfo() throws Exception {
        // given
        User user1 = userRepository.save(UserFixture.createDefaultUser());
        User user2 = userRepository.save(UserFixture.createDefaultUser());
        User user3 = userRepository.save(UserFixture.createDefaultUser());

        Post post1 = postRepository.save(PostFixture.createDefaultPost(user1.getId()));
        List<PollChoice> post1Choices = post1.getPollChoices();
        PollChoice post1Choice1 = post1Choices.get(0);
        PollChoice post1Choice2 = post1Choices.get(1);

        Post post2 = postRepository.save(PostFixture.createDefaultPost(user2.getId()));
        List<PollChoice> post2Choices = post2.getPollChoices();
        PollChoice post2Choice1 = post2Choices.get(0);
        PollChoice post2Choice2 = post2Choices.get(1);

        // 첫 번째 게시글에 투표 생성 - 선택지1: 2표, 선택지2: 1표
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post1.getId(), post1Choice1.getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post1.getId(), post1Choice1.getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post1.getId(), post1Choice2.getId()));

        // 두 번째 게시글에 투표 생성 - 선택지1: 0표, 선택지2: 3표
        voteRepository.save(VoteFixture.createDefaultVote(user1.getId(), post2.getId(), post2Choice2.getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user2.getId(), post2.getId(), post2Choice2.getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user3.getId(), post2.getId(), post2Choice2.getId()));

        // when
        List<PollChoiceVoteInfo> voteInfos = pollChoiceRepository.findPollChoiceWithVoteInfo(Arrays.asList(post1.getId(), post2.getId()));

        // then
        assertThat(voteInfos).hasSize(4);
        assertThat(voteInfos).extracting("postId", "pollChoiceId", "voteCounts", "title")
                .containsExactlyInAnyOrder(
                        tuple(post1.getId(), post1Choice1.getId(), 2L, post1Choice1.getTitle()),
                        tuple(post1.getId(), post1Choice2.getId(), 1L, post1Choice2.getTitle()),
                        tuple(post2.getId(), post2Choice1.getId(), 0L, post2Choice1.getTitle()),
                        tuple(post2.getId(), post2Choice2.getId(), 3L, post2Choice2.getTitle())
                );
    }
}
