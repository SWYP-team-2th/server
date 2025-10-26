package com.chooz.post.application;

import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.domain.Status;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DateCloseSchedulerTest extends IntegrationTest {

    @Autowired
    DateCloseScheduler dateCloseScheduler;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("마감 기간이 지나면 투표 마감되어야 함")
    void closePostsByDate() throws Exception {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .closeOption(PostFixture.createCloseOptionOverDate())
                .build());

        // when
        dateCloseScheduler.closePostsByDate();

        // then
        Post find = postRepository.findById(post.getId()).orElseThrow();
        assertThat(find.getStatus()).isEqualTo(Status.CLOSED);
    }
}
