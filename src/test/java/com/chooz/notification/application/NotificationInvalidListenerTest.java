package com.chooz.notification.application;

import com.chooz.comment.application.CommentService;
import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.commentLike.application.CommentLikeService;
import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.post.application.PostCommandService;
import com.chooz.post.domain.PollChoiceRepository;
import com.chooz.post.domain.Post;
import com.chooz.post.persistence.PostJpaRepository;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.CommentFixture;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.persistence.VoteJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationInvalidListenerTest extends IntegrationTest {

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

    @Autowired
    CommentService commentService;

    @Autowired
    PostCommandService postCommandService;

    @AfterEach
    void tearDown() {
        voteRepository.deleteAllInBatch();
        pollChoiceRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private void commit(Runnable work) {
        if(!TestTransaction.isActive()){
            TestTransaction.start();
        }
        work.run();
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    @DisplayName("댓글좋아요 원 댓글 삭제 시 알림 Invalid 처리")
    void InvalidNotificationByDeleteComment() throws Exception {
        //given
        User receiver = userRepository.save(UserFixture.createDefaultUser());
        User actor = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder().userId(receiver.getId()).build());
        Comment comment =  commentRepository.save(CommentFixture.createCommentBuilder()
                .postId(post.getId())
                .userId(receiver.getId())
                .build());
        commit(() -> commentLikeService.createCommentLike(comment.getId(), actor.getId()));

        //when
        commit(() -> commentService.deleteComment(post.getId(), comment.getId(), receiver.getId()));

        //then
        List<NotificationDto> notifications = notificationQueryRepository.findNotifications(
                receiver.getId(),
                null,
                PageRequest.ofSize(10)
        ).getContent();
        assertAll(
                () -> assertThat(notifications.size()).isZero()
        );
    }
    @Test
    @DisplayName("원 게시물 삭제 시 알림 Invalid 처리")
    void InvalidNotificationByDeletePost() throws Exception {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));

        // when
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User voteUser = userRepository.save(UserFixture.createDefaultUser());
            users.add(voteUser);
            voteRepository.save(VoteFixture.createDefaultVote(voteUser.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        }
        commit(() -> postCommandService.close(user.getId(), post.getId()));

        //when
        commit(() -> postCommandService.delete(user.getId(), post.getId()));

        //then
        List<NotificationDto> notifications = notificationQueryRepository.findNotifications(
                users.get(0).getId(),
                null,
                PageRequest.ofSize(10)
        ).getContent();

        assertAll(
                () -> assertThat(notifications.size()).isZero()
        );
    }
}
