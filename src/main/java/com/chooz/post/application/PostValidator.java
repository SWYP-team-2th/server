package com.chooz.post.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.post.presentation.dto.UpdatePostRequest;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final VoteRepository voteRepository;

    public void validateUpdate(Post post, Long userId, UpdatePostRequest request) {
        CloseOption closeOption = post.getCloseOption();
        CloseType closeType = closeOption.getCloseType();

        if (!post.isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        if (post.isClosed()) {
            throw new BadRequestException(ErrorCode.POST_ALREADY_CLOSED);
        }

        LocalDateTime newClosedAt = request.closeOption().closedAt();
        Integer newMaxVoterCount = request.closeOption().maxVoterCount();
        if (closeType == CloseType.DATE) {
            validateUpdateClosedAt(post, newClosedAt, newMaxVoterCount);
        } else if (closeType == CloseType.VOTER) {
            validateUpdateMaxVoter(post, newClosedAt, newMaxVoterCount);
        }
    }

    private void validateUpdateMaxVoter(Post post, LocalDateTime newClosedAt, Integer newMaxVoterCount) {
        if (Objects.nonNull(newClosedAt) || Objects.isNull(newMaxVoterCount)) {
            throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
        }
        long voterCount = voteRepository.countVoterByPostId(post.getId());
        if (newMaxVoterCount < 1 || newMaxVoterCount > 999) {
            throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
        }
        if (newMaxVoterCount < voterCount) {
            throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
        }
    }

    private static void validateUpdateClosedAt(Post post, LocalDateTime newClosedAt, Integer newMaxVoterCount) {
        if (Objects.isNull(newClosedAt) || Objects.nonNull(newMaxVoterCount)) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_CLOSE_OPTION);
        }
        if (newClosedAt.isBefore(LocalDateTime.now()) || newClosedAt.isBefore(post.getCreatedAt().plusHours(1))) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_CLOSE_OPTION);
        }
    }
}
