package com.chooz.vote.application;

import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.Post;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.presentation.dto.VoteResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VoteResultReader {

    private final RatioCalculator ratioCalculator;

    public List<VoteResultResponse> getVoteResult(List<Vote> totalVoteList, Post post) {
        int totalVoteCount = totalVoteList.size();
        Map<PollChoice, Long> pollChoiceVoteCountMap = getPollChoiceVoteCountMap(totalVoteList, post);
        return pollChoiceVoteCountMap.entrySet().stream()
                .map(entry -> getVoteResultResponse(entry, totalVoteCount))
                .sorted(Comparator.comparingLong(VoteResultResponse::voteCount).reversed())
                .toList();
    }

    private Map<PollChoice, Long> getPollChoiceVoteCountMap(List<Vote> totalVoteList, Post post) {
        return post.getPollChoices().stream()
                .collect(Collectors.toMap(
                        pollChoice -> pollChoice,
                        pollChoice -> totalVoteList.stream()
                                .filter(vote -> vote.getPollChoiceId().equals(pollChoice.getId()))
                                .count()
                ));
    }

    private VoteResultResponse getVoteResultResponse(Map.Entry<PollChoice, Long> entry, int totalVoteCount) {
        PollChoice pollChoice = entry.getKey();
        Long voteCount = entry.getValue();
        String ratio = ratioCalculator.calculate(totalVoteCount, voteCount);
        return new VoteResultResponse(
                pollChoice.getId(),
                pollChoice.getTitle(),
                pollChoice.getImageUrl(),
                voteCount,
                ratio
        );
    }
}
