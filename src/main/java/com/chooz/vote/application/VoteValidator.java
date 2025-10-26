package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.PollChoice;
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

    public void validateIsVotable(Post post, List<Long> pollChoiceIds) {
        validatePost(post);
        if (post.isSingleVote()) {
            validateSingleVote(pollChoiceIds);
        } else {
            validateMultipleVotes(pollChoiceIds);
        }
        validatePollChoiceId(post, pollChoiceIds);
    }

    private void validatePost(Post post) {
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

    private void validateSingleVote(List<Long> pollChoiceIds) {
        if (pollChoiceIds.size() > 1) {
            throw new BadRequestException(ErrorCode.SINGLE_POLL_ALLOWS_MAXIMUM_ONE_CHOICE);
        }
    }

    private void validateMultipleVotes(List<Long> pollChoiceIds) {
        if (pollChoiceIds.size() != pollChoiceIds.stream().distinct().count()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_POLL_CHOICE);
        }
    }

    private void validatePollChoiceId(Post post, List<Long> pollChoiceIds) {
        if (pollChoiceIds.isEmpty()) {
            return;
        }
        List<Long> existingPollChoiceIds = post.getPollChoices()
                .stream()
                .map(PollChoice::getId)
                .toList();

        boolean hasInvalidChoiceId = pollChoiceIds.stream()
                .noneMatch(existingPollChoiceIds::contains);

        if (hasInvalidChoiceId) {
            throw new BadRequestException(ErrorCode.NOT_POST_POLL_CHOICE_ID);
        }
    }

    public void validateVoteResultAccess(Long userId, Post post, List<Vote> totalVoteList) {
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
