package com.chooz.notification.application;

import com.chooz.notification.application.dto.NotificationDto;
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
import org.springframework.data.domain.Slice;
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

//    @Test
//    @DisplayName("투표참여 알림")
//    void onVoted() throws Exception {
//        //given
//        User receiver = userRepository.save(UserFixture.createDefaultUser());
//        User actor = userRepository.save(UserFixture.createDefaultUser());
//        Post post = postRepository.save(PostFixture.createPostBuilder().userId(receiver.getId()).build());
//
//        //when
//        voteService.vote(
//                actor.getId(),
//                post.getId(),
//                post.getPollChoices().stream().map(PollChoice::getId).limit(1).collect(Collectors.toList()));
//
//        TestTransaction.flagForCommit();
//        TestTransaction.end();
//
//        //then
//        Slice<NotificationDto> notificationSlice = notificationQueryRepository.findNotifications(
//                receiver.getId(),
//                null,
//                PageRequest.ofSize(10)
//        );
//
//        assertAll(
//                () -> assertThat(notificationSlice.getContent().size()).isEqualTo(1),
//                () -> assertThat(notificationSlice.getContent().getFirst().receiverId()).isEqualTo(receiver.getId()),
//                () -> assertThat(notificationSlice.getContent().getFirst().actorId()).isEqualTo(actor.getId()),
//                () -> assertThat(notificationSlice.getContent().getFirst().targetType()).isEqualTo(TargetType.VOTE),
//                () -> assertThat(notificationSlice.getContent().getFirst().targetId()).isEqualTo(post.getId()),
//                () -> assertThat(notificationSlice.getContent().getFirst().postId()).isNull()
//        );
//    }
}
