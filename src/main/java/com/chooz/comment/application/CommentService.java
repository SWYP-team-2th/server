package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentLike;
import com.chooz.comment.domain.CommentLikeRepository;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.*;
import com.chooz.comment.support.CommentValidator;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.PostRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentValidator commentValidator;


    public CursorBasePaginatedResponse<CommentResponse> getComments(Long postId, Long userId, CommentCursor cursor, int size) {
        //페이징, size+1개 조회
        Pageable pageable = PageRequest.of(0, size + 1);
        //투표상세 페이지의 댓글들 조회
        List<Comment> comments = commentRepository.findCommentsByPostIdWithPriority(
                postId,
                userId,
                cursor.id() == null ? null : cursor.id(),
                cursor.priority() == null ? null : cursor.priority(),
                pageable
        );
        //댓글 페이징할 거 더 있는지 확인
        boolean hasNext = comments.size() > size;
        if (hasNext) { //더 있으면 한개 제거 하고 리턴
            comments.removeLast(); // 다음 페이지용 1개 제거
        }

        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        Map<Long, Long> likeCountMap = commentLikeRepository.countByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(
                        CommentLikeCountProjection::getCommentId,
                        CommentLikeCountProjection::getLikeCount
                ));

        Map<Long, Boolean> likedMap = Optional.ofNullable(userId)
                .map(id -> commentLikeRepository.findByCommentIdInAndUserId(commentIds, id).stream()
                        .collect(Collectors.toMap(
                                CommentLike::getCommentId,
                                cl -> true
                        ))
                ).orElse(Collections.emptyMap());

        List<CommentResponse> responseContent = comments.stream()
                .map(   comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
                    return new CommentResponse(
                            comment.getId(),
                            comment.getUserId(),
                            user.getNickname(),
                            user.getProfileUrl(),
                            comment.getContent(),
                            comment.getEdited() ? 1 : 0,
                            likeCountMap.getOrDefault(comment.getId(), 0L).intValue(),
                            likedMap.getOrDefault(comment.getId(), false),
                            new CommentCursor(comment.getId(), comment.getUserId().equals(userId) ? 0 : 1) //(내 댓글 = 0, 남 댓글 = 1)
                    );
                }
                )
                .toList();

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                responseContent,
                pageable,
                hasNext));
    }

    @Transactional
    public CommentAnchorResponse createComment(Long postId, CommentRequest commentRequest, Long userId) {
        Comment commentForSave = Comment.create(
                postRepository.findById(postId).orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND)).getId(),
                userRepository.findById(userId).orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND)).getId(),
                commentRequest.content()
        );
        Comment commentFromSave = commentRepository.save(commentForSave);
        return new CommentAnchorResponse(commentFromSave.getId(), commentFromSave.getContent(), "comment-"+ commentFromSave.getId());
    }

    @Transactional
    public CommentAnchorResponse modifyComment(Long postId, Long commentId, CommentRequest commentRequest, Long userId) {
        Comment commentForUpdate = commentRepository.findById(commentId).orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));
        commentValidator.validateCommentAccess(commentForUpdate, postId, userId);
        commentForUpdate.updateComment(commentRequest.content());
        return new CommentAnchorResponse(commentForUpdate.getId(), commentForUpdate.getContent(),"comment-" + commentForUpdate.getId());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        Comment commentForDelete = commentRepository.findById(commentId).orElseThrow(() -> new BadRequestException(ErrorCode.COMMENT_NOT_FOUND));
        commentValidator.validateCommentAccess(commentForDelete, postId, userId);
        commentRepository.delete(commentForDelete);
    }

    @Transactional
    public void createLikeComment(Long commentId, Long userId) {
        boolean alreadyLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        if (alreadyLiked) {
            return;
        }
        commentLikeRepository.save(new CommentLike(null, commentId, userId));
    }

    @Transactional
    public void deleteLikeComment(Long commentId, Long userId) {
        commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(commentLikeRepository::delete);
    }
}
