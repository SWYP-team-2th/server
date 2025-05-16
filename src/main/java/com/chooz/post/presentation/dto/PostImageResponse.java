package com.chooz.post.presentation.dto;

public record PostImageResponse(
        Long id,
        String imageName,
        String imageUrl,
        String thumbnailUrl,
        Long voteId
) {
}
