package com.chooz.comment.presentation.dto;


public record CommentLikeDto(
        long id,
        boolean liked,
        int likeCount
) {
    public static CommentLikeDto of(long id, boolean liked, int likeCount) {
        return new CommentLikeDto(id, liked, likeCount);
    }
}
