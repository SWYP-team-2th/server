package com.chooz.post.application;

import com.chooz.comment.domain.CommentRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.UpdatePostResponse;
import com.chooz.post.presentation.dto.AuthorDto;
import com.chooz.post.application.dto.FeedDto;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.post.presentation.dto.PollChoiceVoteResponse;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final MyPagePostManager myPagePostManager;

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
        List<PollChoiceVoteResponse> pollChoiceVoteResponseList = createPollChoiceResponse(
                userId,
                post.getPollChoices(),
                voteList
        );

        return PostResponse.of(post, author, pollChoiceVoteResponseList, isAuthor, commentCount, voterCount);
    }

    private List<PollChoiceVoteResponse> createPollChoiceResponse(Long userId, List<PollChoice> pollChoices, List<Vote> voteList) {
        return pollChoices.stream()
                .map(pollChoice -> new PollChoiceVoteResponse(
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

    public CursorBasePaginatedResponse<MyPagePostResponse> findUserPosts(
            Long userId,
            Long myPageUserId,
            Long cursor,
            int size
    ) {
        return myPagePostManager.getUserPosts(userId, myPageUserId, cursor, Pageable.ofSize(size));
    }

    public CursorBasePaginatedResponse<MyPagePostResponse> findVotedPosts(
            Long userId,
            Long myPageUserId,
            Long cursor,
            int size
    ) {
        return myPagePostManager.getVotedPosts(userId, myPageUserId, cursor, Pageable.ofSize(size));
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        Slice<FeedDto> postSlice = postRepository.findFeed(cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(post -> createFeedResponse(userId, post)));
    }

    private FeedResponse createFeedResponse(Long userId, FeedDto feedDto) {
        AuthorDto author = new AuthorDto(feedDto.postUserId(), feedDto.nickname(), feedDto.profileUrl());
        boolean isAuthor = feedDto.postUserId().equals(userId);
        return FeedResponse.of(feedDto, author, isAuthor);
    }

    public UpdatePostResponse findUpdatePost(Long userId, Long postId) {
        Post post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        return UpdatePostResponse.of(post);
    }
}
