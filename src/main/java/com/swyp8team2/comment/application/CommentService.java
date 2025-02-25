package com.swyp8team2.comment.application;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.comment.domain.Comment;
import com.swyp8team2.comment.domain.CommentRepository;
import com.swyp8team2.comment.presentation.dto.CommentResponse;
import com.swyp8team2.comment.presentation.dto.CreateCommentRequest;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(Long postId, CreateCommentRequest request, UserInfo userInfo) {
        Comment comment = new Comment(postId, userInfo.userId(), request.content());
        commentRepository.save(comment);
    }

    public CursorBasePaginatedResponse<CommentResponse> findComments(Long postId, Long cursor, int size) {
        Slice<Comment> commentSlice = commentRepository.findByPostId(postId, cursor, PageRequest.of(0, size));
        return CursorBasePaginatedResponse.of(commentSlice.map(this::createCommentResponse));
    }

    private CommentResponse createCommentResponse(Comment comment) {
        User user = userRepository.findById(comment.getUserNo())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return CommentResponse.of(comment, user);
    }
}
