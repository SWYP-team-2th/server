package com.chooz.auth.application;

import com.chooz.auth.application.jwt.JwtService;
import com.chooz.auth.application.oauth.OAuthService;
import com.chooz.auth.domain.SocialAccount;
import com.chooz.auth.domain.SocialAccountRepository;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.notification.domain.NotificationRepository;
import com.chooz.post.application.PostCommandService;
import com.chooz.post.persistence.PostJpaRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WithdrawHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SocialAccountRepository socialAccountRepository;
    private final PostJpaRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final PostCommandService postCommandService;
    private final OAuthService oAuthService;

    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        jwtService.removeToken(userId);
        notificationRepository.deleteAllByUserId(userId);
        postRepository.findAllByUserId(userId)
                .forEach(post -> postCommandService.delete(userId, post.getId()));

        socialAccountRepository.findByUserId(userId).ifPresent(socialAccount -> {
            socialAccountRepository.deleteByUserId(userId);
            oAuthService.withdraw(socialAccount.getSocialId());
        });
        userRepository.delete(user);
    }
}
