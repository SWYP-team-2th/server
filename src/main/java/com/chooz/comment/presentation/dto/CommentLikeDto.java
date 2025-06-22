package com.chooz.comment.presentation.dto;


public record CommentLikeDto(
        Long commentLikeId,
        boolean liked,
        int likeCount
) {
    public static CommentLikeDto of(Long id, boolean liked, int likeCount) {
        return new CommentLikeDto(id, liked, likeCount);
    }
}
