package com.chooz.post.application;

import com.chooz.comment.domain.CommentRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PollChoiceRepository;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.*;
import com.chooz.thumbnail.domain.Thumbnail;
import com.chooz.thumbnail.domain.ThumbnailRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;
    private final PollChoiceRepository pollChoiceRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final ThumbnailRepository thumbnailRepository;

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

    public CursorBasePaginatedResponse<SimplePostResponse> findUserPosts(Long userId, Long cursor, int size) {
        Slice<Post> postSlice = postRepository.findByUserId(userId, cursor, PageRequest.ofSize(size));
        return getCursorPaginatedResponse(postSlice);
    }

    public CursorBasePaginatedResponse<SimplePostResponse> findVotedPosts(Long userId, Long cursor, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<Long> votedPostIds = voteRepository.findByUserId(user.getId())
                .map(Vote::getPostId)
                .toList();
        Slice<Post> votedPostSlice = postRepository.findByIdIn(votedPostIds, cursor, PageRequest.ofSize(size));

        return getCursorPaginatedResponse(votedPostSlice);
    }

    private CursorBasePaginatedResponse<SimplePostResponse> getCursorPaginatedResponse(Slice<Post> postSlice) {
        List<Long> postIds = postSlice.getContent()
                .stream()
                .map(Post::getId)
                .toList();

        List<Thumbnail> thumbnails = thumbnailRepository.findByPostIdIn(postIds);

        List<SimplePostResponse> responseContent = postSlice.getContent().stream()
                .map(post -> getSimplePostResponse(post, thumbnails))
                .toList();

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                responseContent,
                postSlice.getPageable(),
                postSlice.hasNext()
        ));
    }

    private SimplePostResponse getSimplePostResponse(Post post, List<Thumbnail> imageIds) {
        Thumbnail postThumbnail = imageIds.stream()
                .filter(thumbnail -> thumbnail.isThumbnailOf(post.getId()))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.THUMBNAIL_NOT_FOUND));
        return SimplePostResponse.of(post, postThumbnail.getThumbnailUrl());
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
