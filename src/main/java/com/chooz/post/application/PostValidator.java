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

        // 추후에 voterCount 수정 후 리팩터링
        if (closeType == CloseType.DATE) {
            LocalDateTime newClosedAt = request.closeOption().closedAt();
            if (newClosedAt.isBefore(LocalDateTime.now()) || newClosedAt.isBefore(post.getCreatedAt().plusHours(1))) {
                throw new BadRequestException(ErrorCode.INVALID_DATE_CLOSE_OPTION);
            }
        } else if (closeType == CloseType.VOTER) {
            int newMaxVoterCount = request.closeOption().maxVoterCount();
            long voterCount = voteRepository.countVoterByPostId(post.getId());
            if (newMaxVoterCount < voterCount) {
                throw new BadRequestException(ErrorCode.INVALID_VOTER_CLOSE_OPTION);
            }
        }
    }
}
