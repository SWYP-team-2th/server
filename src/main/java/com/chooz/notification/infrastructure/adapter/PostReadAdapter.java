package com.chooz.notification.infrastructure.adapter;

import com.chooz.notification.application.port.PostReadPort;
import com.chooz.notification.application.port.view.PostView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostReadAdapter implements PostReadPort {

    private final PostViewRepository postViewRepository;

    @Override
    public Optional<PostView> getPost(Long commentId) {
        return postViewRepository.findViewById(commentId);
    }
}
