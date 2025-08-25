package com.chooz.support.fixture;

import com.chooz.user.domain.User;

import static com.chooz.support.fixture.OnboardingStepFixture.createDefaultOnboardingStep;

public class UserFixture {

    public static User createDefaultUser() {
        return createUserBuilder().build();
    }
    public static User createUserWithNickname (String nickname) {
        return createUserBuilder().nickname(nickname).build();
    }

    public static User.UserBuilder createUserBuilder() {
        return User.builder()
                .nickname("nickname")
                .profileUrl("https://cdn.chooz.com/default_profile.png")
                .notification(false)
                .onboardingStep(createDefaultOnboardingStep());
    }
}
