package com.chooz.user.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Entity
@Table(name = "user_onboarding_step")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserOnboardingStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private OnboardingStep step;

    @Builder
    private UserOnboardingStep(Long id, User user, OnboardingStep step) {
        this.id = id;
        this.user = user;
        this.step = step;
    }
    public static UserOnboardingStep create(User user, OnboardingStep step){
        return new UserOnboardingStep(null, user, step);
    }
    public void setUser(User user) {
        validateNull(user);
        this.user = user;
    }
}
