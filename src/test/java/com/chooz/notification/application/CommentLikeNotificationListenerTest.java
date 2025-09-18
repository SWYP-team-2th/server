package com.chooz.notification.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.commentLike.application.CommentLikeService;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.TargetType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.CommentFixture;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.transaction.TestTransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentLikeNotificationListenerTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    NotificationQueryRepository notificationQueryRepository;

    @Autowired
    CommentLikeService commentLikeService;

    @Test
    @DisplayName("댓글좋아요 알림")
    void onCommentLiked() throws Exception {
        //given
        User receiver = userRepository.save(UserFixture.createDefaultUser());
        User actor = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder().userId(receiver.getId()).build());
        Comment comment =  commentRepository.save(CommentFixture.createCommentBuilder()
                .userId(receiver.getId())
                .postId(post.getId())
                .build());

        //when
        commentLikeService.createCommentLike(comment.getId(), actor.getId());
        TestTransaction.flagForCommit();
        TestTransaction.end();

        //then
        Slice<Notification> notificationSlice = notificationQueryRepository.findNotifications(
                receiver.getId(),
                null,
                PageRequest.ofSize(10));

        assertAll(
                () -> assertThat(notificationSlice.getContent().size()).isEqualTo(1),
                () -> assertThat(notificationSlice.getContent().getFirst().getUserId()).isEqualTo(receiver.getId()),
                () -> assertThat(notificationSlice.getContent().getFirst().getActorId()).isEqualTo(actor.getId()),
                () -> assertThat(notificationSlice.getContent().getFirst().getTarget().getType()).isEqualTo(TargetType.COMMENT),
                () -> assertThat(notificationSlice.getContent().getFirst().getTarget().getId()).isEqualTo(comment.getId()),
                () -> assertThat(notificationSlice.getContent().getFirst().getType()).isEqualTo(NotificationType.COMMENT_LIKED)
        );
    }
}
