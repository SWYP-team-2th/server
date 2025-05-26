package com.chooz.post.application;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.common.exception.InternalServerException;
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
import com.chooz.post.presentation.dto.AuthorDto;
import com.chooz.post.presentation.dto.FeedDto;
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
    private final ImageFileRepository imageFileRepository;
    private final VoteRepository voteRepository;

    public PostResponse findByShareUrl(Long userId, String shareUrl) {
//        String decrypt = shareUrlBase62Encryptor.decrypt(shareUrl);
        return findById(userId, Long.valueOf("decrypt"));
    }

    public PostResponse findById(Long userId, Long postId) {
        Post post = postRepository.findByIdFetchPollChoices(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        User author = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        List<PollChoiceResponse> votes = createPollChoiceResponse(userId, post);
        boolean isAuthor = post.getUserId().equals(userId);
        return PostResponse.of(post, author, votes, isAuthor);
    }

    private List<PollChoiceResponse> createPollChoiceResponse(Long userId, Post post) {
        List<PollChoice> images = post.getPollChoices();
        return images.stream()
                .map(image -> createVoteResponseDto(image, userId))
                .toList();
    }

    private PollChoiceResponse createVoteResponseDto(PollChoice image, Long userId) {
        ImageFile imageFile = imageFileRepository.findById(image.getImageFileId())
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return new PollChoiceResponse(
                image.getId(),
                image.getName(),
                imageFile.getImageUrl(),
                imageFile.getThumbnailUrl(),
                getVoteId(image, userId)
        );
    }

    private Long getVoteId(PollChoice image, Long userId) {
        return voteRepository.findByUserIdAndPollChoiceId(userId, image.getId())
                .map(Vote::getId)
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
        List<Long> bestPickedImageIds = postSlice.getContent().stream()
                .map(Post::getBestPickedImage)
                .map(PollChoice::getImageFileId)
                .toList();
        List<ImageFile> imageIds = imageFileRepository.findByIdIn(bestPickedImageIds);

        List<SimplePostResponse> responseContent = postSlice.getContent().stream()
                .map(post -> getSimplePostResponse(post, imageIds))
                .toList();

        return CursorBasePaginatedResponse.of(new SliceImpl<>(
                responseContent,
                postSlice.getPageable(),
                postSlice.hasNext()
        ));
    }

    private SimplePostResponse getSimplePostResponse(Post post, List<ImageFile> imageIds) {
        ImageFile bestPickedImage = imageIds.stream()
                .filter(imageFile -> imageFile.getId().equals(post.getBestPickedImage().getImageFileId()))
                .findFirst()
                .orElseThrow(() -> new InternalServerException(ErrorCode.IMAGE_FILE_NOT_FOUND));
        return SimplePostResponse.of(post, bestPickedImage.getThumbnailUrl());
    }

    public CursorBasePaginatedResponse<FeedResponse> findFeed(Long userId, Long cursor, int size) {
        Slice<FeedDto> postSlice = postRepository.findFeedByScopeWithUser(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(postSlice.map(post -> createFeedResponse(userId, post)));
    }

    private FeedResponse createFeedResponse(Long userId, FeedDto dto) {
        AuthorDto author = new AuthorDto(dto.postUserId(), dto.nickname(), dto.profileUrl());
        List<PollChoiceResponse> pollChoices = pollChoiceRepository.findByPostId(userId, dto.postId());
        boolean isAuthor = dto.postUserId().equals(userId);
        return FeedResponse.of(dto, author, pollChoices, isAuthor);
    }
}
