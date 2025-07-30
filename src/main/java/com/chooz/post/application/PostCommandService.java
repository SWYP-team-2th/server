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
import com.chooz.post.presentation.dto.UpdatePostRequest;
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
    private final PostValidator postValidator;

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
                pollChoices.getFirst().getImageUrl(),
                pollChoices,
                shareUrl,
                PollOption.create(
                        request.pollOption().pollType(),
                        request.pollOption().scope(),
                        request.pollOption().commentActive()
                ),
                CloseOption.create(
                        request.closeOption().closeType(),
                        request.closeOption().closedAt(),
                        request.closeOption().maxVoterCount()
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

    @Transactional
    public void update(Long userId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        postValidator.validateUpdate(post, userId, request);

        post.update(
                userId,
                request.title(),
                request.description(),
                PollOption.create(
                        request.pollOption().pollType(),
                        request.pollOption().scope(),
                        request.pollOption().commentActive()
                ),
                new CloseOption( // 수정 예정
                        request.closeOption().closeType(),
                        request.closeOption().closedAt(),
                        request.closeOption().maxVoterCount()
                )
        );
    }
}
