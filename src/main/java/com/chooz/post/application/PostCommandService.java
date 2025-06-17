package com.chooz.post.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.CreatePostRequest;
import com.chooz.post.presentation.dto.CreatePostResponse;
import com.chooz.thumbnail.domain.Thumbnail;
import com.chooz.thumbnail.domain.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;
    private final ShareUrlService shareUrlService;
    private final ThumbnailRepository thumbnailRepository;

    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        Post post = createPost(userId, request);
        savePostThumbnail(post);
        return new CreatePostResponse(post.getId(), post.getShareUrl());
    }

    private Post createPost(Long userId, CreatePostRequest request) {
        List<PollChoice> pollChoices = createPollChoices(request);
        String shareUrl = shareUrlService.generateShareUrl();
        Post post = Post.create(
                userId,
                request.title(),
                request.description(),
                pollChoices,
                shareUrl,
                PollOption.create(
                        request.pollOptions().pollType(),
                        request.pollOptions().scope(),
                        request.pollOptions().commentActive()
                ),
                CloseOption.create(
                        request.closeOptions().closeType(),
                        request.closeOptions().closedAt(),
                        request.closeOptions().maxVoterCount()
                )
        );
        return postRepository.save(post);
    }

    private List<PollChoice> createPollChoices(CreatePostRequest request) {
        return request.pollChoices()
                .stream()
                .map(pollChoiceDto -> PollChoice.create(
                        pollChoiceDto.title(), pollChoiceDto.imageUrl()
                ))
                .collect(Collectors.toList());
    }

    private void savePostThumbnail(Post post) {
        PollChoice thumbnailPollChoice = post.getPollChoices().getFirst();
        thumbnailRepository.save(
                Thumbnail.create(post.getId(), thumbnailPollChoice.getId(), thumbnailPollChoice.getImageUrl())
        );
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        if (!post.isAuthor(userId)) {
            throw new BadRequestException(ErrorCode.NOT_POST_AUTHOR);
        }
        postRepository.delete(post);
    }

    @Transactional
    public void close(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.closeByAuthor(userId);
    }
}
