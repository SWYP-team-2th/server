package com.chooz.post.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.post.application.dto.FeedDto;
import com.chooz.post.domain.Status;

import java.time.LocalDateTime;

public record FeedResponse(
        Long id,
        AuthorDto author,
        Status status,
        String title,
        String thumbnailUrl,
        boolean isAuthor,
        Long voterCount,
        Long commentCount,
        LocalDateTime createdAt

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
                feedDto.commentCount(),
                feedDto.createdAt()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
