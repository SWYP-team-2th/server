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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;
    private final ShareUrlService shareUrlService;

    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        List<PollChoice> pollChoices = createPollChoices(request);
        Post post = Post.create(
                userId,
                request.title(),
                request.description(),
                pollChoices,
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
        String shareUrl = shareUrlService.generateShareUrl();
        Post save = postRepository.save(post);
//        save.setShareUrl(shareUrlBase62Encryptor.encrypt(String.valueOf(save.getId())));
        return new CreatePostResponse(save.getId(), save.getShareUrl());
    }

    private List<PollChoice> createPollChoices(CreatePostRequest request) {
        PollChoiceNameGenerator nameGenerator = new PollChoiceNameGenerator();
        return request.pollChoices().stream()
                .map(voteRequestDto -> PollChoice.create(
                        nameGenerator.generate(),
                        voteRequestDto.imageFileId()
                )).toList();
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
