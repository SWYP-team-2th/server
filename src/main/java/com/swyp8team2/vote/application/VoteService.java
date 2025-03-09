package com.swyp8team2.vote.application;

import com.swyp8team2.common.exception.BadRequestException;
import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.post.domain.Post;
import com.swyp8team2.post.domain.PostRepository;
import com.swyp8team2.post.domain.VoteType;
import com.swyp8team2.user.domain.User;
import com.swyp8team2.user.domain.UserRepository;
import com.swyp8team2.vote.domain.Vote;
import com.swyp8team2.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long vote(Long voterId, Long postId, Long imageId) {
        Optional<Vote> existsVote = voteRepository.findByUserIdAndPostImageId(voterId, imageId);
        if (existsVote.isPresent()) {
            return existsVote.get().getId();
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.validateProgress();

        User voter = userRepository.findById(voterId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

        VoteType voteType = post.getVoteType();
        if (VoteType.SINGLE.equals(voteType)) {
            deleteVoteIfExisting(post, voter.getId());
        }
        Vote vote = createVote(post, imageId, voter.getId());
        return vote.getId();
    }

    private void deleteVoteIfExisting(Post post, Long userId) {
        voteRepository.findByUserIdAndPostId(userId, post.getId())
                        .ifPresent(vote -> {
                            voteRepository.delete(vote);
                            post.cancelVote(vote.getPostImageId());
                        });
    }

    private Vote createVote(Post post, Long imageId, Long userId) {
        Vote vote = voteRepository.save(Vote.of(post.getId(), imageId, userId));
        post.vote(imageId);
        return vote;
    }
}
