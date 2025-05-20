package com.chooz.post.presentation.dto;

public record PollChoiceResponse(
        Long id,
        String imageName,
        String imageUrl,
        String thumbnailUrl,
        Long voteId
) {
}
