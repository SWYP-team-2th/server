package com.chooz.user.presentation.dto;

import com.chooz.user.domain.OnboardingStep;
import com.chooz.user.domain.Role;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserOnboardingStep;

import java.util.List;

public record UserMyInfoResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        List<OnboardingStep> onboardingSteps,
        boolean notification
) {
    public static UserMyInfoResponse of(User user) {
        return new UserMyInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl(),
                user.getOnboardingSteps().stream().map(UserOnboardingStep::getStep).toList(),
                user.isNotification()
        );
    }
}
