package com.chooz.vote.application;

import com.chooz.common.domain.BaseEntity;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@Transactional
@RequiredArgsConstructor
public class VoteWriter {

    private final VoteRepository voteRepository;

    public List<Long> vote(Long voterId, Long postId, List<Long> pollChoiceIds) {
        List<Vote> existingVotes = voteRepository.findByUserIdAndPostId(voterId, postId);

        List<Vote> newVotes = createNewVotes(voterId, postId, pollChoiceIds, existingVotes);

        deleteUnselectedVotes(pollChoiceIds, existingVotes);

        return getTotalVoteIds(existingVotes, newVotes);
    }

    private void deleteUnselectedVotes(List<Long> pollChoiceIds, List<Vote> existingVotes) {
        existingVotes.stream()
                .filter(existingVote -> isUnselectedVote(pollChoiceIds, existingVote))
                .forEach(BaseEntity::delete);
    }

    private boolean isUnselectedVote(List<Long> pollChoiceIds, Vote existingVote) {
        return pollChoiceIds.stream().noneMatch(pollChoiceId -> pollChoiceId.equals(existingVote.getPollChoiceId()));
    }

    private List<Vote> createNewVotes(Long voterId, Long postId, List<Long> pollChoiceIds, List<Vote> existingVotes) {
        List<Long> newVotePollChoiceIds = pollChoiceIds.stream()
                .filter(pollChoiceId -> isNewVotePollChoiceId(pollChoiceId, existingVotes))
                .toList();

        return newVotePollChoiceIds.stream()
                .map(pollChoiceId -> voteRepository.save(Vote.create(voterId, postId, pollChoiceId)))
                .toList();
    }

    private boolean isNewVotePollChoiceId(Long pollChoiceId, List<Vote> existingVotes) {
        return existingVotes.stream()
                .noneMatch(v -> v.getPollChoiceId().equals(pollChoiceId));
    }

    private List<Long> getTotalVoteIds(List<Vote> existingVotes, List<Vote> newVotes) {
        return Stream.of(existingVotes, newVotes)
                .flatMap(Collection::stream)
                .map(Vote::getId)
                .distinct()
                .toList();
    }

}
