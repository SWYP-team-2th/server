package com.swyp8team2.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.swyp8team2.common.util.Validator.validateEmptyString;
import static com.swyp8team2.common.util.Validator.validateNull;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String email;

    public User(Long id, String nickname, String email) {
        validateNull(nickname, email);
        validateEmptyString(nickname, email);
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }

    public static User create(String nickname, String email) {
        return new User(null, nickname, email);
    }
}
