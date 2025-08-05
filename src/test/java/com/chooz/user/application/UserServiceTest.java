package com.chooz.user.application;

import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.*;
import com.chooz.user.presentation.dto.OnboardingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
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
    @DisplayName("유저생성 테스트")
    void createUser() {
        // given
        User user = User.create(null, "https://cdn.chooz.site/default_profile.png");

        nicknameAdjectiveRepository.save(new NicknameAdjective("호기심 많은"));
        nicknameAdjectiveRepository.save(new NicknameAdjective("배려 깊은"));

        // when
        Long userId = userService.createUser(user.getNickname(), user.getProfileUrl());
        Optional<User> returnUser = userRepository.findById(userId);
        // when then
        assertAll(
                () -> assertThat(returnUser.get().getNickname()).isNotNull(),
                () -> assertThat(returnUser.get().getNickname()).contains("츄")
        );
    }
    @Test
    @DisplayName("유저생성 닉넥임 중복 테스트")
    void createUser_duplicateNickname() {
        // given
        User user = User.create(null, "https://cdn.chooz.site/default_profile.png");

        nicknameAdjectiveRepository.save(new NicknameAdjective("호기심 많은"));

        // when
        Long userId1 = userService.createUser(user.getNickname(), user.getProfileUrl());
        Long userId2 = userService.createUser(user.getNickname(), user.getProfileUrl());

        User returnUser1 = userRepository.findById(userId1).get();
        User returnUser2 = userRepository.findById(userId2).get();

        // when then
        assertAll(
                () -> assertThat(returnUser1.getNickname()).isNotNull(),
                () -> assertThat(returnUser1.getNickname()).contains("츄"),
                () -> assertThat(returnUser1.getNickname()).isNotEqualTo(returnUser2.getNickname())
        );
    }
    @Test
    @DisplayName("온보딩 수행 테스트")
    void usser_complete_onboarding_step() {
        // given
        User user = UserFixture.createDefaultUser();
        Long userId = userService.createUser(user.getNickname(), user.getProfileUrl());
        OnboardingRequest onboardingRequest = new OnboardingRequest(
                Map.of(
                        OnboardingStepType.WELCOME_GUIDE, true,
                        OnboardingStepType.FIRST_VOTE, false
                )
        );
        // when
       userService.completeStep(userId, onboardingRequest);
       OnboardingStep onboardingStep
               = userRepository.findById(userId).get().getOnboardingStep();
        // then
        assertAll(
                () -> assertThat(onboardingStep.isWelcomeGuide()).isTrue(),
                () -> assertThat(onboardingStep.isFirstVote()).isFalse()
        );
    }
}
