package com.chooz.post.presentation.dto;

import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.Scope;
import com.chooz.post.domain.Status;
import com.chooz.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String description,
        AuthorDto author,
        List<PollChoiceResponse> pollChoices,
        String shareUrl,
        boolean isAuthor,
        Status status,
        PollOptionDto pollOptions,
        CloseOptionDto closeOptions,
        long commentCount,
        long voterCount,
        LocalDateTime createdAt
) {

    public record PollOptionDto(
            PollType pollType,

            Scope scope,

            CommentActive commentActive
    ) { }

    public static PostResponse of(
            Post post,
            User user,
            List<PollChoiceResponse> pollChoices,
            boolean isAuthor,
            long commentCount,
            long voterCount
    ) {
        PollOption pollOption = post.getPollOption();
        CloseOption closeOption = post.getCloseOption();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                AuthorDto.of(user),
                pollChoices,
                post.getShareUrl(),
                isAuthor,
                post.getStatus(),
                new PollOptionDto(
                        pollOption.getPollType(),
                        pollOption.getScope(),
                        pollOption.getCommentActive()
                ),
                new CloseOptionDto(
                        closeOption.getCloseType(),
                        closeOption.getClosedAt(),
                        closeOption.getMaxVoterCount()
                ),
                commentCount,
                voterCount,
                post.getCreatedAt()
        );
    }
}
