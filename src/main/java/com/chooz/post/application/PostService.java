package com.chooz.post.application;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.presentation.dto.CreatePostRequest;
import com.chooz.post.presentation.dto.CreatePostResponse;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.post.presentation.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Transactional
    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        return postCommandService.create(userId, request);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        postCommandService.delete(userId, postId);
    }

    @Transactional
    public void close(Long userId, Long postId) {
        postCommandService.close(userId, postId);
    }

    public PostResponse findById(Long userId, Long postId) {
        return postQueryService.findById(userId, postId);
    }

    public CursorBasePaginatedResponse<MyPagePostResponse> findUserPosts(Long userId, Long cursor, int size) {
        return postQueryService.findUserPosts(userId, cursor, size);
    }

    public CursorBasePaginatedResponse<MyPagePostResponse> findVotedPosts(Long userId, Long cursor, int size) {
        return postQueryService.findVotedPosts(userId, cursor, size);
    }

    public PostResponse findByShareUrl(Long userId, String shareUrl) {
        return postQueryService.findByShareUrl(userId, shareUrl);
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        return postQueryService.findFeed(userId, cursor, size);
    }
}