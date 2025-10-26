package com.chooz.post.application.dto;

import com.chooz.post.domain.Post;
import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record PostWithVoteCount(
        Post post,
        long voteCount
) {
}
