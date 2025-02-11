package com.swyp8team2.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.swyp8team2.common.util.Validator.validateEmptyString;
import static com.swyp8team2.common.util.Validator.validateNull;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String email;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    public SocialAccount(Long id, Long userId, String socialId, Provider provider, String email) {
        validateNull(userId, socialId, provider, email);
        validateEmptyString(socialId, email);
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.socialId = socialId;
        this.provider = provider;
    }

    public static SocialAccount create(Long userId, String socialId, Provider provider, String email) {
        return new SocialAccount(null, userId, socialId, provider, email);
    }
}
