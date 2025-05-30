package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PostRepository;
import com.chooz.vote.presentation.dto.PollChoiceStatusResponse;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RatioCalculator ratioCalculator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long vote(Long voterId, Long postId, Long pollChoiceId) {
        Optional<Vote> existsVote = voteRepository.findByUserIdAndPollChoiceId(voterId, pollChoiceId);
        if (existsVote.isPresent()) {
            return existsVote.get().getId();
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.validateProgress();

        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        PollType voteType = post.getPollOption().getPollType();
        if (PollType.SINGLE.equals(voteType)) {
            deleteVoteIfExisting(post, voter.getId());
        }

        Vote vote = voteRepository.save(Vote.create(post.getId(), pollChoiceId, voterId));
//        eventPublisher.publishEvent(
//                new VoteEvent(this, post.getId(), pollChoiceId, voterId)
//        );

        //closetype이 date일 경우
        //closeat 관리 테이블 따로 분리해야 할 듯
        //분 주기로 스케줄링

        //closeType이 maxVoterCount일 경우
        //count 조회해서 처리

        return vote.getId();
    }

    private void deleteVoteIfExisting(Post post, Long userId) {
        List<Vote> votes = voteRepository.findByUserIdAndPostId(userId, post.getId());
        for (Vote vote : votes) {
            voteRepository.delete(vote);
            post.cancelVote(vote.getPollChoiceId());
        }
    }

    private Vote createVote(Post post, Long pollChoiceId, Long userId) {
        Vote vote = voteRepository.save(Vote.create(post.getId(), pollChoiceId, userId));
        post.vote(pollChoiceId);
        return vote;
    }

    @Transactional
    public void cancelVote(Long userId, Long voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.VOTE_NOT_FOUND));
        if (!vote.isVoter(userId)) {
            throw new BadRequestException(ErrorCode.NOT_VOTER);
        }
        voteRepository.delete(vote);
        Post post = postRepository.findById(vote.getPostId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.cancelVote(vote.getPollChoiceId());
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

//    private int getTotalVoteCount(List<PollChoice> images) {
//        int totalVoteCount = 0;
//        for (PollChoice image : images) {
//            totalVoteCount += image.getVoteCount();
//        }
//        return totalVoteCount;
//    }
}
