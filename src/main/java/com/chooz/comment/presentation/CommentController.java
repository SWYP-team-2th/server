package com.chooz.comment.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.comment.application.CommentService;
import com.chooz.comment.presentation.dto.CommentAnchorResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    //댓글조회(무한스크롤)
    @GetMapping("")
    public ResponseEntity<CursorBasePaginatedResponse<CommentResponse>> getComments(
            @PathVariable("postId") Long postId,
            @RequestParam(value = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {

        return ResponseEntity.ok(commentService.getComments(postId, userInfo.userId(), cursor, size));
    }
    //댓글생성
    @PostMapping("")
    public ResponseEntity<CommentAnchorResponse> createComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(commentService.createComment(postId, commentRequest, userInfo.userId()));
    }
    //댓글수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentAnchorResponse> modifyComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(commentService.modifyComment(postId, commentId,commentRequest, userInfo.userId()));
    }
    //댓글삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.deleteComment(postId, commentId, userInfo.userId());
    }
    //댓글좋아요
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> createLikeComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.createLikeComment(commentId, userInfo.userId());
        return ResponseEntity.ok().build();
    }
    //댓글좋아요 취소
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> deleteLikeComment(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentService.deleteLikeComment(commentId, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
