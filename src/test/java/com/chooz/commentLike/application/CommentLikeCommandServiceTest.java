package com.chooz.commentLike.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.commentLike.domain.CommentLike;
import com.chooz.commentLike.domain.CommentLikeRepository;
import com.chooz.commentLike.presentation.dto.CommentLikeIdResponse;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.CommentFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CommentLikeCommandServiceTest extends IntegrationTest {

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeService commentLikeService;

    @Test
    @DisplayName("댓글좋아요 생성")
    void createCommentLike() {
        //given, when
        CommentLike commentLike = createAndGetSavedCommentLike();
        //then
        assertThat(commentLike).isNotNull();
    }

    @Test
    @DisplayName("댓글좋아요 삭제")
    void deleteCommentLike() {
        //given
        CommentLike commentLike = createAndGetSavedCommentLike();

        // when
        commentLikeService.deleteCommentLike(commentLike.getCommentId(),commentLike.getId(), commentLike.getUserId());

        // then
        assertThat(commentLikeRepository.existsById(commentLike.getId())).isFalse();
    }

    private CommentLike createAndGetSavedCommentLike() {
        Comment comment = commentRepository.save(CommentFixture.createCommentBuilder().build());
        User user = userRepository.save(UserFixture.createUserBuilder().build());

        CommentLikeIdResponse commentLikeIdResponse =
                commentLikeService.createCommentLike(comment.getId(), user.getId());

        return commentLikeRepository
                .findById(commentLikeIdResponse.commentLikeId()).orElseThrow();
    }
}
