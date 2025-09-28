package com.chooz.post.application;

import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.application.dto.PostClosedNotificationEvent;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.vote.application.VotedEvent;
import com.chooz.vote.domain.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostVotedEventListener {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final EventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(VotedEvent event) {
        Post post = postRepository.findById(event.postId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        handleClosePost(post);
    }

    private void handleClosePost(Post post) {
        long voterCount = voteRepository.countVoterByPostId(post.getId());
        if (post.isClosableByVoterCount(voterCount)) {
            post.close();
            eventPublisher.publish(new PostClosedNotificationEvent(
                    post.getId(),
                    post.getUserId(),
                    LocalDateTime.now()
                    )
            );
        }
    }
}
