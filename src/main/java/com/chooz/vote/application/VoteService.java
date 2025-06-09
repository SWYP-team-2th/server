package com.chooz.vote.application;

import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.vote.presentation.dto.PollChoiceStatusResponse;
import com.chooz.user.domain.UserRepository;
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
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RatioCalculator ratioCalculator;
    private final EventPublisher eventPublisher;
    private final VoteValidator voteValidator;

    @Transactional
    public Long vote(Long voterId, Long postId, Long pollChoiceId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        voteValidator.validateIsVotablePost(post, voterId);

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
                    .map(vote -> {
                        vote.updatePollChoiceId(pollChoiceId);
                        return vote;
                    }).orElseGet(() -> voteRepository.save(Vote.create(post.getId(), pollChoiceId, voterId)));
        } else {
            return voteRepository.save(Vote.create(post.getId(), pollChoiceId, voterId));
        }
    }

    @Transactional
    public void cancelVote(Long userId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.VOTE_NOT_FOUND));
        vote.validateVoter(userId);

        voteRepository.delete(vote);
    }

    public List<PollChoiceStatusResponse> findVoteStatus(Long userId, Long postId) {
//        Post post = postRepository.findByIdFetchPollChoices(postId)
//                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
//        validateVoteStatus(userId, post);
//        int totalVoteCount = getTotalVoteCount(post.getPollChoices());
//        return post.getPollChoices().stream()
//                .map(image -> {
//                    String ratio = ratioCalculator.calculate(totalVoteCount, image.getVoteCount());
//                    return new PollChoiceStatusResponse(image.getId(), image.getTitle(), image.getVoteCount(), ratio);
//                })
//                .sorted(Comparator.comparingInt(PollChoiceStatusResponse::voteCount).reversed())
//                .toList();
        return null;
    }

    private void validateVoteStatus(Long userId, Post post) {
        List<Vote> votes = voteRepository.findByUserIdAndPostId(userId, post.getId());
        if (!(post.isAuthor(userId) || !votes.isEmpty())) {
            throw new BadRequestException(ErrorCode.ACCESS_DENIED_VOTE_STATUS);
        }
    }

//    private int getTotalVoteCount(List<PollChoice> pollChoices) {
//        int totalVoteCount = 0;
//        for (PollChoice image : pollChoices) {
//            totalVoteCount += image.getVoteCount();
//        }
//        return totalVoteCount;
//    }
}
