package com.chooz.support.fixture;

import com.chooz.user.domain.OnboardingStep;
import com.chooz.user.domain.User;

public class OnboardingStepFixture {

    public static OnboardingStep createDefaultOnboardingStep() {
        return createUserBuilder().build();
    }

    public static OnboardingStep.OnboardingStepBuilder createUserBuilder() {
        return OnboardingStep.builder()
                .welcomeGuide(false)
                .firstVote(false);
    }
}
