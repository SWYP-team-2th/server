package com.chooz.notification.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Actor {
    @Column(name = "actor_id", nullable = false)
    private Long id;

    @Column(name = "actor_nickname", nullable = false)
    private String nickname;

    @Column(name = "actor_profile_url", nullable = false)
    private String profileUrl;

    public static Actor of(Long id, String nickname, String profileUrl) {
        return new Actor(id, nickname, profileUrl);
    }
}
