package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.commentLike.domain.CommentLike;
import com.chooz.commentLike.domain.CommentLikeRepository;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.CommentFixture;
import com.chooz.support.fixture.CommentLikeFixture;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentQueryServiceTest extends IntegrationTest {

    @Autowired
    private CommentQueryService commentQueryService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("댓글 목록 조회 테스트")
    void findComments() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        Comment comment = commentRepository.save(CommentFixture.createDefaultComment(user.getId(), post.getId()));
        CommentLike cl = commentLikeRepository.save(CommentLikeFixture.createDefaultCommentLike(user.getId(), comment.getId()));
        createUserAndCommentLikesTimesOf(comment, 9);

        // when
        CommentResponse response =
                commentQueryService.findComments(post.getId(), user.getId(), null, 10);

        //then
        assertAll(
                () -> assertThat(response.commentCount()).isEqualTo(1),
                () -> assertThat(response.comments().data()).hasSize(1),
                () -> assertThat(response.comments().data().get(0).id()).isEqualTo(comment.getId()),
                () -> assertThat(response.comments().data().get(0).content()).isEqualTo(comment.getContent()),
                () -> assertThat(response.comments().data().get(0).edited()).isFalse(),
                () -> assertThat(response.comments().data().get(0).author().userId()).isEqualTo(user.getId()),
                () -> assertThat(response.comments().data().get(0).author().nickname()).isEqualTo(user.getNickname()),
                () -> assertThat(response.comments().data().get(0).author().profileUrl()).isEqualTo(user.getProfileUrl()),
                () -> assertThat(response.comments().data().get(0).like().commentLikeId()).isEqualTo(cl.getId()),
                () -> assertThat(response.comments().data().get(0).like().likeCount()).isEqualTo(10),
                () -> assertThat(response.comments().data().get(0).like().liked()).isTrue(),
                () -> assertThat(response.comments().hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("댓글 20개 목록 조회 테스트")
    void findComments20() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        Comment comment = commentRepository.save(CommentFixture.createDefaultComment(user.getId(), post.getId()));
        createUserAndCommentsTimesOf(post, 19);
        createUserAndCommentLikesTimesOf(comment, 9);

        // when
        CommentResponse response =
                commentQueryService.findComments(post.getId(), user.getId(), null, 10);

        //then
        assertAll(
                () -> assertThat(response.commentCount()).isEqualTo(20),
                () -> assertThat(response.comments().data()).hasSize(10),
                () -> assertThat(response.comments().hasNext()).isTrue()
        );
    }

    @Test
    @DisplayName("댓글 20개 커서 11 목록 조회 테스트")
    void findComments20Cursor11() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        Comment comment = commentRepository.save(CommentFixture.createDefaultComment(user.getId(), post.getId()));
        createUserAndCommentsTimesOf(post, 19);
        createUserAndCommentLikesTimesOf(comment, 9);

        // when
        CommentResponse response =
                commentQueryService.findComments(post.getId(), user.getId(), 11L, 10);

        //then
        assertAll(
                () -> assertThat(response.commentCount()).isEqualTo(20),
                () -> assertThat(response.comments().data()).hasSize(10),
                () -> assertThat(response.comments().hasNext()).isFalse()
        );
    }


    void createUserAndCommentLikesTimesOf(Comment comment, int times) {
        for (int i = 0; i < times; i++) {
            User user = userRepository.save(UserFixture.createDefaultUser());
            commentLikeRepository.save(CommentLikeFixture.createDefaultCommentLike(user.getId(), comment.getId()));
        }
    }
    private void createUserAndCommentsTimesOf(Post post, int times) {
        for (int i = 0; i < times; i++) {
            User user = userRepository.save(UserFixture.createDefaultUser());
            commentRepository.save(CommentFixture.createDefaultComment(user.getId(), post.getId()));
        }
    }
}
