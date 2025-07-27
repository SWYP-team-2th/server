package com.chooz.user.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.chooz.common.util.Validator.validateNull;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends BaseEntity {

    public static final String DEFAULT_PROFILE_URL = "https://image.chooz.site/default_profile.png";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String profileUrl;

    private boolean notification;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOnboardingStep> onboardingSteps = new ArrayList<>();

    @Builder
    private User(
            Long id,
            String nickname,
            String profileUrl,
            List<UserOnboardingStep> onboardingSteps,
            boolean notification
    ) {
        validateNull(nickname, nickname, onboardingSteps, notification);
        this.id = id;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.onboardingSteps = onboardingSteps;
        onboardingSteps.forEach(step -> step.setUser(this));
        this.notification = notification;
    }

    public static User create(String nickname, String profileUrl, List<UserOnboardingStep>onboardingSteps) {
        return new User(null, nickname, profileUrl, onboardingSteps, false);
    }

}
