package com.chooz.post.application;

import com.chooz.comment.domain.CommentRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.application.dto.PollChoiceVoteInfo;
import com.chooz.post.application.dto.PostWithVoteCount;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PollChoiceRepository;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.AuthorDto;
import com.chooz.post.presentation.dto.FeedDto;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.MostVotedPollChoiceDto;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.post.presentation.dto.PollChoiceResponse;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.application.RatioCalculator;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;
    private final PollChoiceRepository pollChoiceRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final RatioCalculator ratioCalculator;

    public PostResponse findByShareUrl(Long userId, String shareUrl) {
        Post post = postRepository.findByShareUrlFetchPollChoices(shareUrl)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        return createPostResponse(userId, post);
    }

    public PostResponse findById(Long userId, Long postId) {
        Post post = postRepository.findByIdFetchPollChoices(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        return createPostResponse(userId, post);
    }

    private PostResponse createPostResponse(Long userId, Post post) {
        User author = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

        long commentCount = commentRepository.countByPostId(post.getId());
        List<Vote> voteList = voteRepository.findAllByPostId(post.getId());
        long voterCount = voteList.stream()
                .map(Vote::getUserId)
                .distinct()
                .count();
        boolean isAuthor = post.getUserId().equals(userId);
        List<PollChoiceResponse> pollChoiceResponseList = createPollChoiceResponse(
                userId,
                post.getPollChoices(),
                voteList
        );

        return PostResponse.of(post, author, pollChoiceResponseList, isAuthor, commentCount, voterCount);
    }

    private List<PollChoiceResponse> createPollChoiceResponse(Long userId, List<PollChoice> pollChoices, List<Vote> voteList) {
        return pollChoices.stream()
                .map(pollChoice -> new PollChoiceResponse(
                        pollChoice.getId(),
                        pollChoice.getTitle(),
                        pollChoice.getImageUrl(),
                        getVoteId(voteList, pollChoice.getId(), userId)
                ))
                .toList();
    }

    private Long getVoteId(List<Vote> voteList, Long pollChoiceId, Long userId) {
        return voteList.stream()
                .filter(vote -> vote.getPollChoiceId().equals(pollChoiceId) && vote.getUserId().equals(userId))
                .map(Vote::getId)
                .findFirst()
                .orElse(null);
    }

    public CursorBasePaginatedResponse<MyPagePostResponse> findUserPosts(Long userId, Long cursor, int size) {
        Slice<PostWithVoteCount> postSlice = postRepository.findPostsWithVoteCountByUserId(userId, cursor, Pageable.ofSize(size));

        return getCursorPaginatedResponse(postSlice);
    }

    public CursorBasePaginatedResponse<MyPagePostResponse> findVotedPosts(Long userId, Long cursor, int size) {
        Slice<PostWithVoteCount> postSlice = postRepository.findVotedPostsWithVoteCount(userId, cursor, Pageable.ofSize(size));

        return getCursorPaginatedResponse(postSlice);
    }

    private CursorBasePaginatedResponse<MyPagePostResponse> getCursorPaginatedResponse(Slice<PostWithVoteCount> postSlice) {
        if (postSlice.isEmpty()) {
            return CursorBasePaginatedResponse.of(new SliceImpl<>(
                    List.of(),
                    postSlice.getPageable(),
                    false
            ));
        }

        Map<Long, PollChoiceVoteInfo> mostVotedPollChoiceByPostId = getMostVotedPollChoiceByPostId(getPostIds(postSlice));

        List<MyPagePostResponse> response = getMyPagePostResponses(postSlice, mostVotedPollChoiceByPostId);

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                response,
                postSlice.getPageable(),
                postSlice.hasNext()
        ));
    }

    private List<MyPagePostResponse> getMyPagePostResponses(
            Slice<PostWithVoteCount> postSlice,
            Map<Long, PollChoiceVoteInfo> mostVotedPollChoiceByPostId
    ) {
        return postSlice.getContent().stream()
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

    private List<Long> getPostIds(Slice<PostWithVoteCount> postSlice) {
        return postSlice.getContent()
                .stream()
                .map(postWithVoteCount -> postWithVoteCount.post().getId())
                .toList();
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        Slice<FeedDto> postSlice = postRepository.findFeedByScopeWithUser(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(post -> createFeedResponse(userId, post)));
    }

    private FeedResponse createFeedResponse(Long userId, FeedDto feedDto) {
        AuthorDto author = new AuthorDto(feedDto.postUserId(), feedDto.nickname(), feedDto.profileUrl());
        boolean isAuthor = feedDto.postUserId().equals(userId);
        return FeedResponse.of(feedDto, author, isAuthor);
    }
}
