package com.chooz.comment.application;

import com.chooz.comment.domain.CommentLikeRepository;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.comment.support.CommentValidator;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.domain.PostRepository;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CommentQueryService commentQueryService;

    public CursorBasePaginatedResponse<CommentResponse> findComments(Long postId, Long userId, long cursorId, int size) {
        return commentQueryService.findComments(postId, userId, cursorId, size);
    }
}
