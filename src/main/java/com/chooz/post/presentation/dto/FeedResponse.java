package com.chooz.post.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.post.domain.Status;

import java.util.List;

public record FeedResponse(
        Long id,
        AuthorDto author,
        List<PollChoiceResponse> images,
        Status status,
        String description,
        String shareUrl,
        boolean isAuthor,
        Long participantCount,
        Long commentCount

) implements CursorDto {

    public static FeedResponse of(FeedDto feedDto, AuthorDto author, List<PollChoiceResponse> images, boolean isAuthor) {
        return new FeedResponse(
                feedDto.postId(),
                author,
                images,
                feedDto.status(),
                feedDto.description(),
                feedDto.shareUrl(),
                isAuthor,
                feedDto.participantCount(),
                feedDto.commentCount()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
