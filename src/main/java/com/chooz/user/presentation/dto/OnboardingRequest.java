package com.chooz.user.presentation.dto;

import com.chooz.user.domain.OnboardingStepType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record OnboardingRequest(
        @NotNull
        @Size(min = 1)
        Map<OnboardingStepType, Boolean> onboardingStep
) {}
