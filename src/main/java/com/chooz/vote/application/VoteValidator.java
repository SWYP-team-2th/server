package com.chooz.vote.application;

import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

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
                long voterCount = voteRepository.countVoterByPostId(post.getId());
                post.validateMaxVoterCount(voterCount);
            }
        }
    }
}
