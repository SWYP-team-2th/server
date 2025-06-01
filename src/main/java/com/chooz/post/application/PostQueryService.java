package com.chooz.post.application;

import com.chooz.comment.domain.CommentRepository;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.image.domain.ImageFile;
import com.chooz.image.domain.ImageFileRepository;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PollChoiceRepository;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.PollChoiceResponse;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.post.presentation.dto.SimplePostResponse;
import com.chooz.post.presentation.dto.FeedDto;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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
//        List<Long> bestPickedImageIds = postSlice.getContent().stream()
//                .map(Post::getBestPickedImage)
//                .map(PollChoice::getImageFileId)
//                .toList();
//        List<ImageFile> imageIds = imageFileRepository.findByIdIn(bestPickedImageIds);
//
//        List<SimplePostResponse> responseContent = postSlice.getContent().stream()
//                .map(post -> getSimplePostResponse(post, imageIds))
//                .toList();
//
//        return CursorBasePaginatedResponse.of(new SliceImpl<>(
//                responseContent,
//                postSlice.getPageable(),
//                postSlice.hasNext()
//        ));
        return null;
    }

    private SimplePostResponse getSimplePostResponse(Post post, List<ImageFile> imageIds) {
//        ImageFile bestPickedImage = imageIds.stream()
//                .filter(imageFile -> imageFile.getId().equals(post.getBestPickedImage().getImageFileId()))
//                .findFirst()
//                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
//        return SimplePostResponse.of(post, bestPickedImage.getThumbnailUrl());
        return null;
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        Slice<FeedDto> postSlice = postRepository.findFeedByScopeWithUser(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(post -> createFeedResponse(userId, post)));
    }

    private FeedResponse createFeedResponse(Long userId, FeedDto dto) {
//        AuthorDto author = new AuthorDto(dto.postUserId(), dto.nickname(), dto.profileUrl());
//        List<PollChoiceResponse> pollChoices = pollChoiceRepository.findByPostId(userId, dto.postId());
//        boolean isAuthor = dto.postUserId().equals(userId);
//        return FeedResponse.of(dto, author, pollChoices, isAuthor);
        return null;
    }
}
