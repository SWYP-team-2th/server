package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentLike;
import com.chooz.comment.domain.CommentLikeRepository;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentAnchorResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.comment.support.CommentValidator;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest  {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentValidator commentValidator;

    @Test
    void createComment_성공() {

        // given
        Long postId = 1L;
        Long userId = 2L;
        String content = "테스트 댓글을 입니다.";

        CommentRequest request = new CommentRequest(content);

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        Comment savedComment = Comment.builder()
                .id(10L)
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(mockPost));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

        // when
        CommentAnchorResponse response = commentService.createComment(postId, request, userId);

        // then
        assertThat(response.commentId()).isEqualTo(savedComment.getId());
        assertThat(response.anchor()).isEqualTo("comment-" + savedComment.getId());
    }

    @Test
    void createComment_존재하지_않는_포스트() {
        // given
        Long postId = 999L;
        Long userId = 1L;
        CommentRequest request = new CommentRequest("content");

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(postId, request, userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    void 댓글_수정_성공() {
        // given
        Long commentId = 1L;
        Long postId = 10L;
        Long userId = 100L;
        String originalContent = "원래 댓글";
        String updatedContent = "수정된 댓글";

        Comment comment = Comment.create(postId, userId, originalContent);
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        willDoNothing().given(commentValidator).validateCommentAccess(comment, postId, userId);

        // when
        CommentAnchorResponse result = commentService.modifyComment(
                postId,
                commentId,
                new CommentRequest(updatedContent),
                userId
        );
        // then
        assertThat(result.commentId()).isEqualTo(commentId);
        assertThat(result.content()).isEqualTo(updatedContent);
        assertThat(result.anchor()).isEqualTo("comment-" + commentId);
    }

    @Test
    void 댓글_삭제_성공() {
        // given
        Long commentId = 1L;
        Long postId = 10L;
        Long userId = 100L;

        Comment comment = Comment.create(postId, userId, "삭제할 댓글");
        ReflectionTestUtils.setField(comment, "id", commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        willDoNothing().given(commentValidator).validateCommentAccess(comment, postId, userId);
        willDoNothing().given(commentRepository).delete(comment);

        // when
        commentService.deleteComment(postId, commentId, userId);

        // then
        then(commentRepository).should().delete(comment);
    }
    @Test
    void 댓글_좋아요_생성_성공() {
        // given
        Long commentId = 1L;
        Long userId = 100L;

        given(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).willReturn(false);

        // when
        commentService.createLikeComment(commentId, userId);

        // then
        then(commentLikeRepository).should().save(any(CommentLike.class));
    }

    @Test
    void 댓글_좋아요_삭제_성공() {
        // given
        Long commentId = 1L;
        Long userId = 100L;

        CommentLike mockLike = new CommentLike(999L, commentId, userId);

        given(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
                .willReturn(Optional.of(mockLike));

        // when
        commentService.deleteLikeComment(commentId, userId);

        // then
        then(commentLikeRepository).should().delete(mockLike);
    }

}
