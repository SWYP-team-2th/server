package com.chooz.commentLike.presentation.dto;

import software.amazon.awssdk.services.s3.endpoints.internal.Value;

public record CommentLikeIdResponse (
   Long commentLikeId,
   Integer likeCount
) {
}
