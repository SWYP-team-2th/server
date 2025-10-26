package com.chooz.notification.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.commentLike.application.CommentLikeService;
import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.TargetType;
import com.chooz.post.domain.PollChoiceRepository;
import com.chooz.post.domain.Post;
import com.chooz.post.persistence.PostJpaRepository;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.CommentFixture;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.persistence.VoteJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.transaction.TestTransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentLikeNotificationListenerTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostJpaRepository postRepository;

    @Autowired
    VoteJpaRepository voteRepository;

    @Autowired
    PollChoiceRepository pollChoiceRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NotificationQueryRepository notificationQueryRepository;

    @Autowired
    CommentLikeService commentLikeService;

    @AfterEach
    void tearDown() {
        voteRepository.deleteAllInBatch();
        pollChoiceRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("댓글좋아요 알림")
    void onCommentLiked() throws Exception {
        //given
        User receiver = userRepository.save(UserFixture.createDefaultUser());
        User actor = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder().userId(receiver.getId()).build());
        Comment comment =  commentRepository.save(CommentFixture.createCommentBuilder()
                .postId(post.getId())
                .userId(receiver.getId())
                .build());

        //when
        commentLikeService.createCommentLike(comment.getId(), actor.getId());
        TestTransaction.flagForCommit();
        TestTransaction.end();

        //then
        NotificationDto notification = notificationQueryRepository.findNotifications(
                receiver.getId(),
                null,
                PageRequest.ofSize(10)
        ).getContent().getFirst();

        assertAll(
                () -> assertThat(notification.notificationRowDto().title()).contains("좋아요를 눌렀어요!"),
                () -> assertThat(notification.notificationRowDto().content()).contains("확인해보세요."),
                () -> assertThat(notification.notificationRowDto().profileUrl()).isEqualTo(actor.getProfileUrl()),
                () -> assertThat(notification.targets())
                        .hasSize(2)
                        .anySatisfy(target -> {
                                    assertThat(target.type()).isEqualTo(TargetType.POST);
                                }
                        ),
                () -> assertThat(notification.notificationRowDto().imageUrl()).isEqualTo(post.getImageUrl()),
                () -> assertThat(notification.notificationRowDto().isRead()).isEqualTo(false)
        );
    }
}
