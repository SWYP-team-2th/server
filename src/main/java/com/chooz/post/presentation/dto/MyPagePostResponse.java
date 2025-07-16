package com.chooz.post.presentation.dto;

import com.chooz.common.dto.CursorDto;
import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.Status;

import java.time.LocalDateTime;

public record MyPagePostResponse(
        long id,
        String title,
        String thumbnailImageUrl,
        Status status,
        CloseOptionDto closeOptionDto,
        PostVoteInfo postVoteInfo,
        LocalDateTime createdAt
) implements CursorDto {

    public record PostVoteInfo(
            long totalVoterCount,
            MostVotedPollChoiceDto mostVotedPollChoice
    ) {
    }

    public static MyPagePostResponse of(PostWithVoteCount postWithVoteCount, MostVotedPollChoiceDto mostVotedPollChoiceDto) {
        Post post = postWithVoteCount.post();
        long totalVoterCount = postWithVoteCount.voteCount();
        CloseOption closeOption = post.getCloseOption();
        return new MyPagePostResponse(
                post.getId(),
                post.getTitle(),
                post.getImageUrl(),
                post.getStatus(),
                new CloseOptionDto(closeOption.getCloseType(), closeOption.getClosedAt(), closeOption.getMaxVoterCount()),
                new PostVoteInfo(totalVoterCount, mostVotedPollChoiceDto),
                post.getCreatedAt()
        );
    }

    @Override
    public long getId() {
        return id;
    }

}
