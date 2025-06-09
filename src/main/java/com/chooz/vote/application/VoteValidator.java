package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VoteValidator {

    private final VoteRepository voteRepository;
    private final Clock clock;

    public void validateIsVotablePost(Post post, Long voterId) {
        CloseOption closeOption = post.getCloseOption();
        CloseType closeType = closeOption.getCloseType();

        post.validateProgress();
        switch (closeType) {
            case DATE -> post.validateCloseDate(clock);
            case VOTER -> {
                long voterCount = voteRepository.countDistinctByPostIdAndUserId(post.getId(), voterId);
                post.validateMaxVoterCount(voterCount);
            }
        }
    }
}
