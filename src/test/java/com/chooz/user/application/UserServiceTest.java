package com.chooz.user.application;

import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.*;
import com.chooz.user.presentation.dto.OnboardingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserServiceTest extends IntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NicknameAdjectiveRepository nicknameAdjectiveRepository;

    @Autowired
    NicknameGenerator nicknameGenerator;

    @Autowired
    UserService userService;

    private void saveNickNameAdjective(String... adjectives) {
        for(String adjective : adjectives){
            nicknameAdjectiveRepository.save(new NicknameAdjective(adjective));
        }
    }
    private User saveUser(){
        User user = UserFixture.createUserWithNickname(nicknameGenerator.generate());
        return userRepository.save(user);
    }
    @Test
    @DisplayName("유저생성 테스트")
    void createUser() {
        // given
        saveNickNameAdjective("호기심 많은", "배려 깊은");
        User user = saveUser();

        // when then
        assertAll(
                () -> assertThat(user.getNickname()).isNotNull(),
                () -> assertThat(user.getNickname()).contains("츄")
        );
    }

    @Test
    @DisplayName("유저생성 닉넥임 중복 테스트")
    void createUser_duplicateNickname() {
        // given
        saveNickNameAdjective("호기심 많은");
        User user = saveUser();
        User user2 = saveUser();

        // when then
        assertAll(
                () -> assertThat(user.getNickname()).isNotNull(),
                () -> assertThat(user.getNickname()).contains("츄"),
                () -> assertThat(user.getNickname()).isNotEqualTo(user2.getNickname()),
                () -> assertThat(user.getNickname()).isEqualTo("호기심 많은 츄"),
                () -> assertThat(user2.getNickname()).isEqualTo("호기심 많은 츄1")
        );
    }
    @Test
    @DisplayName("유저생성 닉넥임 사용가능한 가장 작은 suffix 선택 테스트")
    void createUser_minSuffix() {
        // given
        saveNickNameAdjective("호기심 많은");
        User user = saveUser();
        User user1 = saveUser();
        User user2 = saveUser();
        userRepository.delete(user1);

        // when
        User user3 = saveUser();

        // when then
        assertAll(
                () -> assertThat(user.getNickname()).isEqualTo("호기심 많은 츄"),
                () -> assertThat(user2.getNickname()).isEqualTo("호기심 많은 츄2"),
                () -> assertThat(user3.getNickname()).isEqualTo("호기심 많은 츄1")
        );
    }

    @Test
    @DisplayName("온보딩 수행 테스트")
    void user_complete_onboarding_step() {
        // given
        Long userId = saveUser().getId();
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
