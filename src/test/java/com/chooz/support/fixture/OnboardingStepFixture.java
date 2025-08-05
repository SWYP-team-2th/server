package com.chooz.support.fixture;

import com.chooz.user.domain.Role;
import com.chooz.user.domain.User;

import java.util.List;
import java.util.Map;

public class UserFixture {

    public static User createDefaultUser() {
        return createUserBuilder().build();
    }

    public static User.UserBuilder createUserBuilder() {
        return User.builder()
                .nickname("nickname")
                .profileUrl("http://example.com/profile.png")
                .notification(false)
                .onboardingStep(
                )

    }
}
