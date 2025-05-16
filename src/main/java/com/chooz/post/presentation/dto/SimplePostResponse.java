package com.chooz.post.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.post.domain.Post;

import java.time.LocalDateTime;

public record SimplePostResponse(
        long id,
        String bestPickedImageUrl,
        String shareUrl,
        LocalDateTime createdAt
) implements CursorDto {

    public static SimplePostResponse of(Post post, String bestPickedImageUrl) {
        return new SimplePostResponse(
                post.getId(),
                bestPickedImageUrl,
                post.getShareUrl(),
                post.getCreatedAt()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
