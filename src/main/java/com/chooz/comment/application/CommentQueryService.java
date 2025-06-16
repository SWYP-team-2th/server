package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.commentLike.domain.CommentLike;
import com.chooz.commentLike.domain.CommentLikeCountProjection;
import com.chooz.commentLike.domain.CommentLikeRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;


    public CursorBasePaginatedResponse<CommentResponse> findComments(Long postId, Long userId, Long cursorId, int size) {
        Slice<Comment> comments = commentRepository.findByPostId(postId, cursorId, PageRequest.ofSize(size));

        List<Long> commentIds = findCommentIds(comments);
        List<Long> userIds = findUserIds(comments);

        Map<Long, Long> likeCountCommentMap = findLikeCountCommentMap(commentIds);
        Map<Long, Boolean> likedCommentMap = findLikedCommentMap(commentIds, userId);
        Map<Long, User> userCommentMap = findUserCommentMap(userIds);

        List<CommentResponse> responseContent =
                findResponseContent(comments, userCommentMap, likeCountCommentMap, likedCommentMap);

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                responseContent,
                comments.getPageable(),
                comments.hasNext()
        ));
    }

    private List<Long> findUserIds(Slice<Comment> comments) {
        return comments.getContent().stream()
                .map(Comment::getUserId)
                .distinct()
                .toList();
    }

    private List<Long> findCommentIds(Slice<Comment> comments) {
         return comments.getContent().stream()
                .map(Comment::getId)
                .toList();
    }

    private User findUserByUserMap(Map<Long, User> userCommentMap, Comment comment) {
        return Optional.ofNullable(userCommentMap.get(comment.getUserId()))
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }

    private Map<Long, User> findUserCommentMap(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private Map<Long, Long> findLikeCountCommentMap(List<Long> commentIds) {
        return commentLikeRepository.countByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(
                        CommentLikeCountProjection::getCommentId,
                        CommentLikeCountProjection::getLikeCount
                ));
    }

    private Map<Long, Boolean> findLikedCommentMap(List<Long> commentIds, Long userId) {
        return Optional.ofNullable(userId)
                .map(id -> commentLikeRepository.findByCommentIdInAndUserId(commentIds, id).stream()
                        .collect(Collectors.toMap(
                                CommentLike::getCommentId,
                                cl -> true
                        ))
                ).orElse(Collections.emptyMap());
    }

    private List<CommentResponse> findResponseContent(Slice<Comment> comments,
                                                     Map<Long, User> userCommentMap,
                                                     Map<Long, Long>  likeCountCommentMap,
                                                     Map<Long, Boolean> likedCommentMap) {
        return comments.getContent().stream()
                .map(   comment -> {
                            User user = findUserByUserMap(userCommentMap, comment);
                            return new CommentResponse(
                                    comment.getId(),
                                    comment.getUserId(),
                                    user.getNickname(),
                                    user.getProfileUrl(),
                                    comment.getContent(),
                                    comment.getEdited(),
                                    likeCountCommentMap.getOrDefault(comment.getId(), 0L).intValue(),
                                    likedCommentMap.getOrDefault(comment.getId(), false)
                            );
                        }
                ).toList();
    }
}
