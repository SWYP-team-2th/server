package com.chooz.user.presentation.dto;

import com.chooz.user.domain.OnboardingStep;
import com.chooz.user.domain.OnboardingStepType;
import com.chooz.user.domain.User;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record UserMyInfoResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        boolean notification,
        Map<String, Boolean> onboardingStep

) {
    public static UserMyInfoResponse of(User user) {
        return new UserMyInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl(),
                user.isNotification(),
                convertStepStatus(user.getOnboardingStep())
        );
    }

    private static Map<String, Boolean> convertStepStatus(OnboardingStep step) {
        return Arrays.stream(OnboardingStepType.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        stepType -> step != null && stepType.check(step)
                ));
    }
}
