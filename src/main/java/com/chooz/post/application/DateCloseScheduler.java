package com.chooz.post.application;

import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateCloseScheduler {

    private final PostRepository postRepository;

    @Scheduled(fixedDelay = 1000 * 60)
    public void closePostsByDate() {
        postRepository.findPostsNeedToClose()
                .forEach(Post::close);
    }
}
