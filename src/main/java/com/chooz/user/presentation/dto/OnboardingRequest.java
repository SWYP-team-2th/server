package com.chooz.user.presentation.dto;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.user.domain.OnboardingStep;
import jakarta.validation.constraints.NotNull;

public record OnboardingRequest(
        @NotNull
        OnboardingStep step
) {
        public OnboardingRequest {
                if (step != OnboardingStep.WELCOME_GUIDE && step != OnboardingStep.FIRST_VOTE) {
                        throw new BadRequestException(ErrorCode.INVALID_ONBOARDING_STEP);
                }
        }
}
