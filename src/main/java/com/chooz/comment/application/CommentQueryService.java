package com.chooz.comment.application;

import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentAuthorDto;
import com.chooz.comment.presentation.dto.CommentDto;
import com.chooz.comment.presentation.dto.CommentLikeDto;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.comment.support.CommentValidator;
import com.chooz.commentLike.domain.CommentLike;
import com.chooz.commentLike.domain.CommentLikeCountProjection;
import com.chooz.commentLike.domain.CommentLikeRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.PostRepository;
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
    private final PostRepository postRepository;
    private final CommentValidator commentValidator;


    public CommentResponse findComments(Long postId, Long userId, Long cursorId, int size) {
        commentValidator.validateCommentActive(postRepository.findCommentActiveByPostId(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND)));

        Slice<Comment> comments = commentRepository.findByPostId(postId, cursorId, PageRequest.ofSize(size));


        List<Long> commentIds = findCommentIds(comments);
        List<Long> userIds = findUserIds(comments);

        int commentCount = commentRepository.countByPostId(postId);
        Map<Long, Long> likeCountCommentMap = findLikeCountCommentMap(commentIds);
        List<CommentLike> commentLikes = findCommentLikes(commentIds, userId);
        Map<Long, Boolean> likedCommentMap = findLikedCommentMap(commentLikes);
        Map<Long, Long> likedCommentLikeIdMap = findLikedCommentLikeIdMap(commentLikes);
        Map<Long, User> userCommentMap = findUserCommentMap(userIds);

        List<CommentDto> responseContent =
                findResponseContent(
                        comments,
                        userCommentMap,
                        likedCommentLikeIdMap,
                        likedCommentMap,
                        likeCountCommentMap
                );
        return new CommentResponse(
                commentCount,
                CursorBasePaginatedResponse.of(
                        new SliceImpl<>(
                                responseContent,
                                comments.getPageable(),
                                comments.hasNext()
                        )
                )
        );
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
    private List<CommentLike> findCommentLikes(List<Long> commentIds, Long userId){
        return commentLikeRepository.findByCommentIdInAndUserId(commentIds, userId);
    }
    private Map<Long, Long> findLikedCommentLikeIdMap(List<CommentLike> commentLikes){
        return Optional.ofNullable(commentLikes)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .collect(Collectors.toMap(
                                CommentLike::getCommentId,
                                CommentLike::getId
                        ))
                ).orElse(Collections.emptyMap());
    }
    private Map<Long, Boolean> findLikedCommentMap(List<CommentLike> commentLikes) {
        return Optional.ofNullable(commentLikes)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .collect(Collectors.toMap(
                                CommentLike::getCommentId,
                                cl -> true
                        ))
                ).orElse(Collections.emptyMap());
    }

    private List<CommentDto> findResponseContent(Slice<Comment> comments,
                                                 Map<Long, User> userCommentMap,
                                                 Map<Long, Long> likedCommentLikeIdMap,
                                                 Map<Long, Boolean> likedCommentMap,
                                                 Map<Long, Long>  likeCountCommentMap
                                                 ) {
        return comments.getContent().stream()
                .map(   comment -> {
                            return CommentDto.of(
                                    comment,
                                    CommentAuthorDto.of(findUserByUserMap(userCommentMap, comment)),
                                    CommentLikeDto.of(
                                            likedCommentLikeIdMap.getOrDefault(comment.getId(), null),
                                            likedCommentMap.getOrDefault(comment.getId(), false),
                                            likeCountCommentMap.getOrDefault(comment.getId(), 0L).intValue()
                                    )
                            );
                        }
                ).toList();
    }
}
