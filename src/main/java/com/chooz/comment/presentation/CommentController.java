package com.chooz.comment.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.comment.application.CommentService;
import com.chooz.comment.presentation.dto.CommentIdResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("")
    public ResponseEntity<CursorBasePaginatedResponse<CommentResponse>> findComments(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(commentService.findComments(postId, userInfo.userId(), cursor, size));
    }

    @PostMapping("")
    public ResponseEntity<CommentIdResponse> createComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(commentService.createComment(postId, commentRequest, userInfo.userId()));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentIdResponse> updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentId,commentRequest, userInfo.userId()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.deleteComment(postId, commentId, userInfo.userId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> createLikeComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.createLikeComment(commentId, userInfo.userId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> deleteLikeComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.deleteLikeComment(commentId, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
