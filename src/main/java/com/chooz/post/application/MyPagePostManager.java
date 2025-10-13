package com.chooz.post.application;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.application.dto.PollChoiceVoteInfo;
import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.domain.PollChoiceRepository;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.MostVotedPollChoiceDto;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.vote.application.RatioCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyPagePostManager {

    private final PostRepository postRepository;
    private final PollChoiceRepository pollChoiceRepository;
    private final RatioCalculator ratioCalculator;

    public CursorBasePaginatedResponse<MyPagePostResponse> getUserPosts(
            Long userId,
            Long myPageUserId,
            Long cursor,
            Pageable pageable
    ) {
        Slice<PostWithVoteCount> postSlice = postRepository.findPostsWithVoteCountByUserId(
                userId,
                myPageUserId,
                cursor,
                pageable
        );

        return getMyPageCursoredResponse(postSlice);
    }

    public CursorBasePaginatedResponse<MyPagePostResponse> getVotedPosts(
            Long userId,
            Long myPageUserId,
            Long cursor,
            Pageable pageable
    ) {
        Slice<PostWithVoteCount> postSlice = postRepository.findVotedPostsWithVoteCount(
                userId,
                myPageUserId,
                cursor,
                pageable
        );

        return getMyPageCursoredResponse(postSlice);
    }

    private CursorBasePaginatedResponse<MyPagePostResponse> getMyPageCursoredResponse(Slice<PostWithVoteCount> postSlice) {
        if (postSlice.isEmpty()) {
            return CursorBasePaginatedResponse.of(new SliceImpl<>(
                    List.of(),
                    postSlice.getPageable(),
                    false
            ));
        }

        List<Long> postIds = getPostIds(postSlice);
        Map<Long, PollChoiceVoteInfo> mostVotedPollChoiceByPostId = getMostVotedPollChoiceByPostId(postIds);

        List<MyPagePostResponse> response = getMyPagePostResponses(postSlice, mostVotedPollChoiceByPostId);

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                response,
                postSlice.getPageable(),
                postSlice.hasNext()
        ));
    }

    private Map<Long, PollChoiceVoteInfo> getMostVotedPollChoiceByPostId(List<Long> postIds) {
        List<PollChoiceVoteInfo> pollChoiceWithVoteInfo = pollChoiceRepository.findPollChoiceWithVoteInfo(postIds);
        return pollChoiceWithVoteInfo.stream()
                .collect(Collectors.groupingBy(
                        PollChoiceVoteInfo::postId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                choices -> choices.stream()
                                        .max(Comparator.comparing(PollChoiceVoteInfo::voteCounts))
                                        .orElse(null)
                        )
                ));
    }

    private List<MyPagePostResponse> getMyPagePostResponses(
            Slice<PostWithVoteCount> postSlice,
            Map<Long, PollChoiceVoteInfo> mostVotedPollChoiceByPostId
    ) {
        return postSlice.getContent()
                .stream()
                .map(postWithVoteCount -> {
                    var pollChoiceVoteInfo = mostVotedPollChoiceByPostId.get(postWithVoteCount.post().getId());
                    var mostVotedPollChoiceInfo = MostVotedPollChoiceDto.of(
                            pollChoiceVoteInfo,
                            ratioCalculator.calculate(postWithVoteCount.voteCount(), pollChoiceVoteInfo.voteCounts())
                    );
                    return MyPagePostResponse.of(postWithVoteCount, mostVotedPollChoiceInfo);
                })
                .toList();
    }

    private List<Long> getPostIds(Slice<PostWithVoteCount> postSlice) {
        return postSlice.getContent()
                .stream()
                .map(postWithVoteCount -> postWithVoteCount.post().getId())
                .toList();
    }
}
