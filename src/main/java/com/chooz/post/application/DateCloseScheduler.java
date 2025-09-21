package com.chooz.post.application;

import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DateCloseScheduler {

    private final PostRepository postRepository;

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60)
    public void closePostsByDate() {
        log.info("마감 스케줄링 시작");
        List<Post> postsNeedToClose = postRepository.findPostsNeedToClose();
        //마감 알림
        postsNeedToClose.forEach(Post::close);
        log.info("총 {}개 게시글 마감", postsNeedToClose.size());
    }
}
