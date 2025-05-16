package com.chooz.comment.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.comment.application.CommentService;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.common.dto.CursorBasePaginatedResponse;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<Void> createComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.createComment(postId, request, userInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorBasePaginatedResponse<CommentResponse>> selectComments(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        Long userId = Optional.ofNullable(userInfo).map(UserInfo::userId).orElse(null);
        return ResponseEntity.ok(commentService.findComments(userId, postId, cursor, size));
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.updateComment(commentId, request, userInfo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.deleteComment(commentId, userInfo);
        return ResponseEntity.ok().build();
    }
}
