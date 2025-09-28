package com.chooz.notification.application;

import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.TargetType;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.application.VoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class VotedNotificationListenerTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteService voteService;

    @Autowired
    NotificationQueryRepository notificationQueryRepository;

    @Test
    @DisplayName("투표참여 알림")
    void onVoted() throws Exception {
        //given
        User receiver = userRepository.save(UserFixture.createDefaultUser());
        User actor = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder().userId(receiver.getId()).build());

        //when
        voteService.vote(
                actor.getId(),
                post.getId(),
                post.getPollChoices().stream().map(PollChoice::getId).limit(1).collect(Collectors.toList()));

        TestTransaction.flagForCommit();
        TestTransaction.end();

        //then
        NotificationDto notification = notificationQueryRepository.findNotifications(
                receiver.getId(),
                null,
                PageRequest.ofSize(10)
        ).getContent().getFirst();

        assertAll(
                () -> assertThat(notification.notificationRowDto().title()).contains("투표에 참여했어요!"),
                () -> assertThat(notification.notificationRowDto().content()).contains("확인해보세요."),
                () -> assertThat(notification.notificationRowDto().profileUrl()).isEqualTo(actor.getProfileUrl()),
                () -> assertThat(notification.targets())
                        .hasSize(1)
                        .anySatisfy(target -> {
                                    assertThat(target.id()).isEqualTo(1L);
                                    assertThat(target.type()).isEqualTo(TargetType.POST);
                                }
                        ),
                () -> assertThat(notification.notificationRowDto().imageUrl()).isEqualTo(post.getImageUrl()),
                () -> assertThat(notification.notificationRowDto().isRead()).isEqualTo(false)
        );
    }
}
