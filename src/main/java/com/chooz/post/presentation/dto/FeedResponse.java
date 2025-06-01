package com.chooz.post.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.post.domain.Status;

public record FeedResponse(
        Long id,
        AuthorDto author,
        Status status,
        String title,
        String thumbnailUrl,
        boolean isAuthor,
        Long voterCount,
        Long commentCount

) implements CursorDto {

    public static FeedResponse of(FeedDto feedDto, AuthorDto author, boolean isAuthor) {
        return new FeedResponse(
                feedDto.postId(),
                author,
                feedDto.status(),
                feedDto.title(),
                feedDto.thumbnailUrl(),
                isAuthor,
                feedDto.voterCount(),
                feedDto.commentCount()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
