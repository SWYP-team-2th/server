package com.chooz.commentLike.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.commentLike.application.CommentLikeService;
import com.chooz.commentLike.presentation.dto.CommentLikeIdResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment-likes")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}")
    public ResponseEntity<CommentLikeIdResponse> createCommentLike(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(commentLikeService.createCommentLike(commentId, userInfo.userId()));
    }

    @DeleteMapping("/{commentLikeId}")
    public ResponseEntity<Void> deleteCommentLike(
            @PathVariable("commentLikeId") Long commentLikeId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentLikeService.deleteCommentLike(commentLikeId, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
