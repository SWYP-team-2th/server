package com.chooz.vote.application;

import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.vote.presentation.dto.VoteStatusResponse;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;
    private final VoteValidator voteValidator;
    private final VoteStatusReader voteStatusReader;

    @Transactional
    public Long vote(Long voterId, Long postId, Long pollChoiceId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        voteValidator.validateIsVotablePost(post);

        return voteRepository.findByUserIdAndPollChoiceId(voterId, pollChoiceId)
                .orElseGet(() -> processVote(voterId, pollChoiceId, post))
                .getId();
    }

    private Vote processVote(Long voterId, Long pollChoiceId, Post post) {
        Vote vote = createVote(voterId, pollChoiceId, post);
        eventPublisher.publish(new VotedEvent(post.getId(), pollChoiceId, voterId));
        return vote;
    }

    private Vote createVote(Long voterId, Long pollChoiceId, Post post) {
        if (post.isSingleVote()) {
            return voteRepository.findByUserIdAndPostId(voterId, post.getId()).stream()
                    .findFirst()
                    .map(vote -> updateExistVote(pollChoiceId, vote))
                    .orElseGet(() -> voteRepository.save(Vote.create(post.getId(), pollChoiceId, voterId)));
        } else {
            return voteRepository.save(Vote.create(post.getId(), pollChoiceId, voterId));
        }
    }

    private Vote updateExistVote(Long pollChoiceId, Vote vote) {
        vote.updatePollChoiceId(pollChoiceId);
        return vote;
    }

    @Transactional
    public void cancelVote(Long userId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.VOTE_NOT_FOUND));
        vote.validateVoter(userId);

        voteRepository.delete(vote);
    }

    public List<VoteStatusResponse> findVoteStatus(Long userId, Long postId) {
        Post post = postRepository.findByIdFetchPollChoices(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        List<Vote> totalVoteList = voteRepository.findAllByPostId(postId);
        voteValidator.validateVoteStatusAccess(userId, post, totalVoteList);

        return voteStatusReader.getVoteStatus(totalVoteList, post);
    }
}
