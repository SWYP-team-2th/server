package com.chooz.post.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.application.PostService;
import com.chooz.post.presentation.dto.CreatePostRequest;
import com.chooz.post.presentation.dto.CreatePostResponse;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.post.presentation.dto.UpdatePostRequest;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.UpdatePostResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<PostResponse> findPostById(
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

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        postService.update(userInfo.userId(), postId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/update")
    public ResponseEntity<UpdatePostResponse> updatePost(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        UpdatePostResponse response = postService.findUpdatePost(userInfo.userId(), postId);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<CursorBasePaginatedResponse<MyPagePostResponse>> findMyPosts(
            @PathVariable("userId") Long myPageUserId,
            @AuthenticationPrincipal UserInfo userInfo,
            @RequestParam(name = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) int size
    ) {
        return ResponseEntity.ok(postService.findUserPosts(userInfo.userId(), myPageUserId, cursor, size));
    }

    @GetMapping("/users/{userId}/voted")
    public ResponseEntity<CursorBasePaginatedResponse<MyPagePostResponse>> findVotedPosts(
            @PathVariable("userId") Long myPageUserId,
            @AuthenticationPrincipal UserInfo userInfo,
            @RequestParam(name = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) int size
    ) {
        return ResponseEntity.ok(postService.findVotedPosts(userInfo.userId(), myPageUserId, cursor, size));
    }

    @GetMapping("/feed")
    public ResponseEntity<CursorBasePaginatedResponse<FeedResponse>> findFeed(
            @RequestParam(name = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(postService.findFeed(userInfo.userId(), cursor, size));
    }
}
