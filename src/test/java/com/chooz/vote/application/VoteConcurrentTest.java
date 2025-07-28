package com.chooz.vote.application;

import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("mysql")
@SpringBootTest
class VoteConcurrentTest {

    @Autowired
    VoteService voteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    PostRepository postRepository;

    @AfterEach
    void setUp() {
        voteRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
//    @Disabled
    void concurrentTest() throws Exception {
        // given
        int maxVoterCount = 4;
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                        .userId(user.getId())
                        .closeOption(PostFixture.voterCloseOption(maxVoterCount))
                        .build());
        Long pollChoiceId = post.getPollChoices().getFirst().getId();

        int threadCount = 30;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            users.add(userRepository.save(UserFixture.createDefaultUser()));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    Long voterId = users.get(index).getId();

                    voteService.vote(voterId, post.getId(), List.of(pollChoiceId));
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        List<Vote> voteList = voteRepository.findAllByPostId(post.getId());
        assertThat(voteList).hasSize(maxVoterCount);
    }
}
