package com.chooz.vote.application;

import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import com.chooz.vote.presentation.dto.VoteResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;
    private final VoteValidator voteValidator;
    private final VoteResultReader voteResultReader;
    private final VoteWriter voteWriter;

    @Transactional
    public List<Long> vote(Long voterId, Long postId, List<Long> pollChoiceIds) {
        Post post = postRepository.findByIdFetchPollChoicesWithLock(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        voteValidator.validateIsVotable(post, pollChoiceIds);

        List<Long> voteIds = voteWriter.vote(voterId, postId, pollChoiceIds);

        eventPublisher.publish(new VotedEvent(post.getId(), pollChoiceIds, voterId));
        eventPublisher.publish(new VotedNotificationEvent(postId, voterId, LocalDateTime.now()));
        return voteIds;
    }

    public List<VoteResultResponse> findVoteResult(Long userId, Long postId) {
        Post post = postRepository.findByIdFetchPollChoices(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        List<Vote> totalVoteList = voteRepository.findAllByPostId(postId);
        voteValidator.validateVoteResultAccess(userId, post, totalVoteList);

        return voteResultReader.getVoteResult(totalVoteList, post);
    }

    public void delete(Long postId) {
        voteRepository.deleteAllByPostId(postId);
    }
}
