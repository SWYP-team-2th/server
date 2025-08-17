package com.chooz.user.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final String DEFAULT_PROFILE_URL = "https://cdn.chooz.site/default_profile.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String profileUrl;

    private boolean notification;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "onboarding_step_id", unique = true)
    private OnboardingStep onboardingStep;

    @Builder
    private User(
            Long id,
            String nickname,
            String profileUrl,
            boolean notification,
            OnboardingStep onboardingStep
    ) {
        this.id = id;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.notification = notification;
        this.onboardingStep = onboardingStep;
    }

    public static User create(String nickname, String profileUrl) {
        return new User(null, nickname, getOrDefaultProfileImage(profileUrl), false, new OnboardingStep());
    }

    private static String getOrDefaultProfileImage(String profileImageUrl) {
        return Optional.ofNullable(profileImageUrl)
                .orElse(User.DEFAULT_PROFILE_URL);
    }

    public boolean hasCompletedOnboarding() {
        return onboardingStep != null && onboardingStep.isCompletedAll();
    }
}
