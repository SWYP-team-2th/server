package com.chooz.post.application;

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

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void closePostsByDate() {
        log.info("마감 스케줄링 시작 | 서버 시간: {}", LocalDateTime.now());
        List<Post> postsNeedToClose = postRepository.findPostsNeedToClose();
        postsNeedToClose.forEach(Post::close);
        log.info("총 {}개 게시글 마감", postsNeedToClose.size());
    }
}
