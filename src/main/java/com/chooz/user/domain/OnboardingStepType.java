package com.chooz.user.domain;


import java.util.function.Consumer;
import java.util.function.Predicate;

public enum OnboardingStepType {

    WELCOME_GUIDE(OnboardingStep::completeWelcomeGuide, OnboardingStep::isWelcomeGuide),
    FIRST_VOTE(OnboardingStep::completeFirstVote, OnboardingStep::isFirstVote);

    private final Consumer<OnboardingStep> action;
    private final Predicate<OnboardingStep> checker;

    OnboardingStepType(Consumer<OnboardingStep> action, Predicate<OnboardingStep> checker) {
        this.action = action;
        this.checker = checker;
    }

    public void apply(OnboardingStep step) {
        this.action.accept(step);
    }

    public boolean check(OnboardingStep step) {
       return this.checker.test(step);
    }
}
