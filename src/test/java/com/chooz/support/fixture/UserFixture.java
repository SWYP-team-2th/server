package com.chooz.support.fixture;

import com.chooz.user.domain.Role;
import com.chooz.user.domain.User;

import java.util.List;
import java.util.Map;

import static com.chooz.support.fixture.OnboardingStepFixture.createDefaultOnboardingStep;

public class UserFixture {

    public static User createDefaultUser() {
        return createUserBuilder().build();
    }

    public static User.UserBuilder createUserBuilder() {
        return User.builder()
                .nickname("nickname")
                .profileUrl("https://cdn.chooz.com/default_profile.png")
                .notification(false)
                .onboardingStep(createDefaultOnboardingStep());
    }
}
