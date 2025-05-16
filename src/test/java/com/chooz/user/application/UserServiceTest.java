package com.chooz.user.application;

import com.chooz.support.IntegrationTest;
import com.chooz.user.domain.NicknameAdjective;
import com.chooz.user.domain.NicknameAdjectiveRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserServiceTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NicknameAdjectiveRepository nicknameAdjectiveRepository;

    @Autowired
    UserService userService;

    @Test
    void createUser() {
        // given
        User user = User.create(null, "https://image.com/1");

        for (int i = 0; i < 250; i++) {
            nicknameAdjectiveRepository.save(new NicknameAdjective("호기심 많은 뽀또"));
            nicknameAdjectiveRepository.save(new NicknameAdjective("배려 깊은 뽀또"));
        }

        // when
        Long userId = userService.createUser(user.getNickname(), user.getProfileUrl());
        Optional<User> returnUser = userRepository.findById(userId);

        // when then
        assertAll(
                () -> assertThat(returnUser.get().getNickname()).isNotNull(),
                () -> assertThat(returnUser.get().getNickname()).contains("뽀또")
        );

    }
}