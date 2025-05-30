package com.chooz.user.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(jakarta.persistence.EnumType.STRING)
    public Role role;

    @Builder
    public User(Long id, String nickname, String profileUrl, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
    }

    public static User create(String nickname, String profileUrl) {
        return new User(null, nickname, profileUrl, Role.USER);
    }

    public static User createGuest(String nickname) {
        return new User(
                null,
                nickname,
                DEFAULT_PROFILE_URL,
                Role.GUEST
        );
    }
}
