package com.chooz.user.presentation.dto;

import com.chooz.user.domain.OnboardingStep;
import com.chooz.user.domain.OnboardingStepType;
import com.chooz.user.domain.User;

import java.util.*;
import java.util.stream.Collectors;

public record UserInfoResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        boolean notification,
        Map<String, Boolean> onboardingStep

) {
    public static UserInfoResponse of(User user) {
        return new UserInfoResponse(
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
