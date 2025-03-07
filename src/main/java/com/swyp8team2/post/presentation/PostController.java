package com.swyp8team2.post.presentation;

import com.swyp8team2.auth.domain.UserInfo;
import com.swyp8team2.common.dto.CursorBasePaginatedResponse;
import com.swyp8team2.post.application.PostService;
import com.swyp8team2.post.presentation.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<CreatePostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {

        return ResponseEntity.ok(postService.create(userInfo.userId(), request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> findPost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        Long userId = Optional.ofNullable(userInfo)
                .map(UserInfo::userId)
                .orElse(null);
        return ResponseEntity.ok(postService.findById(userId, postId));
    }

    @GetMapping("/shareUrl/{shareUrl}")
    public ResponseEntity<PostResponse> findPostByShareUrl(
            @PathVariable("shareUrl") String shareUrl,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        Long userId = Optional.ofNullable(userInfo)
                .map(UserInfo::userId)
                .orElse(null);
        return ResponseEntity.ok(postService.findByShareUrl(userId, shareUrl));
    }

    @GetMapping("/{postId}/status")
    public ResponseEntity<List<PostImageVoteStatusResponse>> findVoteStatus(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(postService.findVoteStatus(userInfo.userId(), postId));
    }

    @PostMapping("/{postId}/status")
    public ResponseEntity<Void> toggleStatusPost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/update")
    public ResponseEntity<Void> updatePost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/close")
    public ResponseEntity<Void> closePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        postService.close(userInfo.userId(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponse> deletePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        postService.delete(userInfo.userId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<CursorBasePaginatedResponse<SimplePostResponse>> findMyPosts(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) int size
    ) {
        return ResponseEntity.ok(postService.findUserPosts(userId, cursor, size));
    }

    @GetMapping("/users/{userId}/voted")
    public ResponseEntity<CursorBasePaginatedResponse<SimplePostResponse>> findVotedPosts(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) int size
    ) {
        return ResponseEntity.ok(postService.findVotedPosts(userId, cursor, size));
    }
}
