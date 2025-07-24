package com.chooz.post.application.dto;

import com.chooz.post.domain.Post;

public record PostWithVoteCount(
        Post post,
        long voteCount
) {
}
