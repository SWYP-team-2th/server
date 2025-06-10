package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoteValidator {

    private final VoteRepository voteRepository;
    private final Clock clock;

    public void validateIsVotablePost(Post post) {
        CloseOption closeOption = post.getCloseOption();
        CloseType closeType = closeOption.getCloseType();

        post.validateProgress();
        switch (closeType) {
            case DATE -> post.validateCloseDate(clock);
            case VOTER -> {
                long voterCount = voteRepository.countVoterByPostId(post.getId());
                post.validateMaxVoterCount(voterCount);
            }
        }
    }

    public void validateVoteStatusAccess(Long userId, Post post, List<Vote> totalVoteList) {
        if (post.isClosed()) {
            return;
        }
        validateNotClosedPostVoteStatusAccess(userId, post, totalVoteList);
    }

    private void validateNotClosedPostVoteStatusAccess(Long userId, Post post, List<Vote> totalVoteList) {
        boolean voted = totalVoteList.stream()
                .anyMatch(vote -> vote.getUserId().equals(userId));
        if (!(post.isAuthor(userId) || voted)) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED_VOTE_STATUS);
        }
    }
}
