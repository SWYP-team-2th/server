package com.chooz.user.presentation.dto;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.user.domain.OnboardingStepType;

import java.util.Map;

public record OnboardingRequest(
        Map<OnboardingStepType, Boolean> onboardingStep
) {
        public OnboardingRequest {
                if (onboardingStep == null
                        || onboardingStep.isEmpty()
                        || onboardingStep.values().stream().noneMatch(Boolean.TRUE::equals)
                ) {
                        throw new BadRequestException(ErrorCode.INVALID_ONBOARDING_STEP);
                }
        }
}
