package com.chooz.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOnboardingStepRepository extends JpaRepository<UserOnboardingStep, Long> {
    boolean existsByUserAndStep(User user, OnboardingStep step);
}
