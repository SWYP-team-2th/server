package com.chooz.post.application;

import com.chooz.common.event.EventPublisher;
import com.chooz.post.application.dto.PostClosedNotificationEvent;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DateCloseScheduler {

    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void closePostsByDate() {
        log.info("마감 스케줄링 시작 | 서버 시간: {}", LocalDateTime.now());
        List<Post> postsNeedToClose = postRepository.findPostsNeedToClose();
        postsNeedToClose.forEach(Post::close);
        postsNeedToClose.forEach(
                post -> eventPublisher.publish(new PostClosedNotificationEvent(
                        post.getId(),
                        post.getUserId(),
                        LocalDateTime.now()
                        )
                )
        );
        log.info("총 {}개 게시글 마감", postsNeedToClose.size());
    }
}
