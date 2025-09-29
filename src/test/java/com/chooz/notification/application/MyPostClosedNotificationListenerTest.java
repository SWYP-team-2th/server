package com.chooz.notification.application;

import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.TargetType;
import com.chooz.post.application.DateCloseScheduler;
import com.chooz.post.application.PostCommandService;
import com.chooz.post.application.PostVotedEventListener;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MyPostClosedNotificationListenerTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    NotificationQueryRepository notificationQueryRepository;

    @Autowired
    PostVotedEventListener postVotedEventListener;

    @Autowired
    DateCloseScheduler dateCloseScheduler;

    @Autowired
    PostCommandService postCommandService;

    @Test
    @DisplayName("내 투표 마감 알림(참여자 수 마감)")
    void onMyPostClosedByVoter() throws Exception {
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

        //when
        int voterCount = 5;
        for (int i = 0; i < voterCount; i++) {
            User user = userRepository.save(UserFixture.createDefaultUser());
            voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        }
        postVotedEventListener.handle(new VotedEvent(post.getId(), List.of(post.getPollChoices().get(0).getId()), user1.getId()));
        TestTransaction.flagForCommit();
        TestTransaction.end();

        //then
        NotificationDto notification = notificationQueryRepository.findNotifications(
                user1.getId(),
                null,
                PageRequest.ofSize(10)
        ).getContent().stream().filter(notificationDto ->
                notificationDto.notificationRowDto().notificationType().equals(NotificationType.MY_POST_CLOSED))
                .toList().getFirst();

        assertAll(
                () -> assertThat(notification.notificationRowDto().title()).contains("투표가 마감됐습니다!"),
                () -> assertThat(notification.notificationRowDto().content()).contains("확인해보세요."),
                () -> assertThat(notification.notificationRowDto().profileUrl()).isEqualTo(user1.getProfileUrl()),
                () -> assertThat(notification.targets())
                        .hasSize(1)
                        .anySatisfy(target -> {
                                    assertThat(target.type()).isEqualTo(TargetType.POST);
                                }
                        ),
                () -> assertThat(notification.notificationRowDto().imageUrl()).isEqualTo(post.getImageUrl()),
                () -> assertThat(notification.notificationRowDto().isRead()).isEqualTo(false)
        );
    }
    @Test
    @DisplayName("내 투표 마감 알림(시간 마감)")
    void onMyPostClosedByDate() throws Exception {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .closeOption(PostFixture.createCloseOptionOverDate())
                .build());

        // when
        dateCloseScheduler.closePostsByDate();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        //then
        NotificationDto notification = notificationQueryRepository.findNotifications(
                        user.getId(),
                        null,
                        PageRequest.ofSize(10)
                ).getContent().stream().filter(notificationDto ->
                        notificationDto.notificationRowDto().notificationType().equals(NotificationType.MY_POST_CLOSED))
                .toList().getFirst();

        assertAll(
                () -> assertThat(notification.notificationRowDto().title()).contains("투표가 마감됐습니다!"),
                () -> assertThat(notification.notificationRowDto().content()).contains("확인해보세요."),
                () -> assertThat(notification.notificationRowDto().profileUrl()).isEqualTo(user.getProfileUrl()),
                () -> assertThat(notification.targets())
                        .hasSize(1)
                        .anySatisfy(target -> {
                                    assertThat(target.type()).isEqualTo(TargetType.POST);
                                }
                        ),
                () -> assertThat(notification.notificationRowDto().imageUrl()).isEqualTo(post.getImageUrl()),
                () -> assertThat(notification.notificationRowDto().isRead()).isEqualTo(false)
        );
    }
    @Test
    @DisplayName("내 투표 마감 알림(직접 마감)")
    void onMyPostClosedBySelf() throws Exception {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));

        // when
        postCommandService.close(user.getId(), post.getId());

        TestTransaction.flagForCommit();
        TestTransaction.end();

        //then
        List<NotificationDto> notifications = notificationQueryRepository.findNotifications(
                        user.getId(),
                        null,
                        PageRequest.ofSize(10)
                ).getContent().stream().filter(notificationDto ->
                        notificationDto.notificationRowDto().notificationType().equals(NotificationType.MY_POST_CLOSED))
                .toList();

        assertAll(
                () -> assertThat(notifications).hasSize(0)
        );
    }
}
