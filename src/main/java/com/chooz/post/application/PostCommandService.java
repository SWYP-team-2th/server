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
import com.chooz.post.presentation.dto.PollChoiceRequestDto;
import com.chooz.thumbnail.domain.Thumbnail;
import com.chooz.thumbnail.domain.ThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
                        request.pollOptionDto().pollType(),
                        request.pollOptionDto().scope(),
                        request.pollOptionDto().commentActive()
                ),
                CloseOption.create(
                        request.closeOptionDto().closeType(),
                        request.closeOptionDto().closedAt(),
                        request.closeOptionDto().maxVoterCount()
                )
        );
        return postRepository.save(post);
    }

    private List<PollChoice> createPollChoices(CreatePostRequest request) {
        List<PollChoice> pollChoices = new ArrayList<>();
        List<PollChoiceRequestDto> pollChoiceDtoList = request.pollChoices();
        for (int orderSeq = 0; orderSeq < pollChoiceDtoList.size(); orderSeq++) {
            PollChoiceRequestDto pollChoiceDto = pollChoiceDtoList.get(orderSeq);
            PollChoice pollChoice = PollChoice.create(pollChoiceDto.title(), pollChoiceDto.imageUrl(), orderSeq);
            pollChoices.add(pollChoice);
        }
        return pollChoices;
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
        post.close(userId);
    }

    @Transactional
    public void toggleScope(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        post.toggleScope(userId);
    }
}
