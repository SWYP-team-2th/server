package com.chooz.notification.infrastructure.adapter;

import com.chooz.notification.application.port.UserReadPort;
import com.chooz.notification.application.port.view.UserView;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReadAdapter implements UserReadPort {

    private final UserViewRepository userViewRepository;

    @Override
    public Optional<UserView> getUserByCommentId(Long commentId) {
        return userViewRepository.findViewByCommentId(commentId);
    }

    @Override
    public Optional<UserView> getUser(Long commentId) {
        return userViewRepository.findViewById(commentId);
    }
}
