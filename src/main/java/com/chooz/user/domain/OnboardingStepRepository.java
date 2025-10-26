package com.chooz.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingStepRepository extends JpaRepository<OnboardingStep, Long> {}
