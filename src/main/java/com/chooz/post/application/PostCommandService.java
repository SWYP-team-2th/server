package com.chooz.post.application;

import com.chooz.common.event.DeleteEvent;
import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.application.dto.PostClosedNotificationEvent;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.presentation.dto.CreatePostRequest;
import com.chooz.post.presentation.dto.CreatePostResponse;
import com.chooz.post.presentation.dto.UpdatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;
    private final ShareUrlService shareUrlService;
    private final PostValidator postValidator;
    private final EventPublisher eventPublisher;

    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        Post post = createPost(userId, request);
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

    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.delete(userId);
        eventPublisher.publish(DeleteEvent.of(post.getId(), post.getClass().getSimpleName().toUpperCase()));
    }

    @Transactional
    public void close(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.closeByAuthor(userId);
        eventPublisher.publish(new PostClosedNotificationEvent(
                        post.getId(),
                        post.getUserId(),
                        post.getCloseOption().getCloseType(),
                        LocalDateTime.now()
                )
        );
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
                new CloseOption(
                        request.closeOption().closeType(),
                        request.closeOption().closedAt(),
                        request.closeOption().maxVoterCount()
                )
        );
    }
}
