package com.chooz.user.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@Table(name = "onboarding_step")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OnboardingStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean welcomeGuide;

    private boolean firstVote;

    @Builder
    public OnboardingStep(Long id, boolean welcomeGuide, boolean firstVote) {
        this.id = id;
        this.welcomeGuide = welcomeGuide;
        this.firstVote = firstVote;
    }

    public void completeWelcomeGuide() {
        this.welcomeGuide = true;
    }

    public void completeFirstVote() {
        this.firstVote = true;
    }

    public boolean isCompletedAll() {
        return welcomeGuide && firstVote;
    }
}
