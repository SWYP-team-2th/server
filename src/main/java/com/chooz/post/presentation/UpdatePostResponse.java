package com.chooz.post.presentation;

import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.Status;
import com.chooz.post.presentation.dto.CloseOptionDto;
import com.chooz.post.presentation.dto.PollChoiceResponse;
import com.chooz.post.presentation.dto.PollOptionDto;

import java.time.LocalDateTime;
import java.util.List;

public record UpdatePostResponse(
        Long id,
        String title,
        String description,
        List<PollChoiceResponse> pollChoices,
        String shareUrl,
        Status status,
        PollOptionDto pollOption,
        CloseOptionDto closeOption,
        LocalDateTime createdAt
) {

    public static UpdatePostResponse of(Post post) {
        PollOption pollOption = post.getPollOption();
        CloseOption closeOption = post.getCloseOption();
        return new UpdatePostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getPollChoices()
                        .stream()
                        .map(pollChoice -> new PollChoiceResponse(
                            pollChoice.getId(),
                            pollChoice.getTitle(),
                            pollChoice.getImageUrl()
                )).toList(),
                post.getShareUrl(),
                post.getStatus(),
                new PollOptionDto(
                        pollOption.getScope(),
                        pollOption.getPollType(),
                        pollOption.getCommentActive()
                ),
                new CloseOptionDto(
                        closeOption.getCloseType(),
                        closeOption.getClosedAt(),
                        closeOption.getMaxVoterCount()
                ),
                post.getCreatedAt()
        );
    }
}
