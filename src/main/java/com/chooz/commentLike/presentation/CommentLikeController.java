package com.chooz.commentLike.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.commentLike.application.CommentLikeService;
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
    public ResponseEntity<Void> createCommentLike(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentLikeService.createCommentLike(commentId, userInfo.userId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentLike(
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        commentLikeService.deleteCommentLike(commentId, userInfo.userId());
        return ResponseEntity.noContent().build();
    }
}
