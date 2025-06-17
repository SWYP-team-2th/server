package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CommentCommandServiceTest extends IntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment() {
        //given
        Comment comment = makeComment();

        //when
        Comment savedComment = createAndGetSavedComment(comment);

        //then
        assertAll(
                () -> assertThat(comment.getUserId()).isEqualTo(savedComment.getUserId()),
                () -> assertThat(comment.getPostId()).isEqualTo(savedComment.getPostId()),
                () -> assertThat(comment.getContent()).isEqualTo(savedComment.getContent()),
                () -> assertThat(comment.getEdited()).isEqualTo(savedComment.getEdited())
        );
    }

    @Test
    @DisplayName("댓글 수정")
    void updateComment() {
        //given
        Comment comment = makeComment();
        Comment savedComment = createAndGetSavedComment(comment);
        CommentRequest updatedRequest = makeRequest();

        //when
        Long updateCommentId = commentService.updateComment(
                savedComment.getPostId(),
                savedComment.getId(),
                updatedRequest,
                savedComment.getUserId()).commentId();

        // then
        Comment updatedComment = commentRepository.findById(updateCommentId).orElseThrow();
        assertAll(
                () -> assertThat(updatedComment.getId()).isEqualTo(savedComment.getId()),
                () -> assertThat(updatedComment.getContent()).isEqualTo(updatedRequest.content()),
                () -> assertThat(updatedComment.getEdited()).isTrue()
        );
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteComment() {
        // given
        Comment comment = makeComment();
        Comment savedComment = createAndGetSavedComment(comment);

        // when
        commentService.deleteComment(savedComment.getPostId(), savedComment.getId(), savedComment.getUserId());

        // then
        assertThat(commentRepository.findById(savedComment.getId())).isEmpty();
    }

    private Comment makeComment() {
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        return CommentFixture.createCommentBuilder()
                .userId(user.getId())
                .postId(post.getId())
                .build();
    }

    private Comment createAndGetSavedComment(Comment comment) {
        CommentRequest request = new CommentRequest(comment.getContent());
        Long commentId = commentService.createComment(
                comment.getPostId(),
                request,
                comment.getUserId()).commentId();
        return commentRepository.findById(commentId).orElseThrow();
    }

    private CommentRequest makeRequest() {
        return new CommentRequest("This is a updated content");
    }
}
