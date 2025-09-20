package com.chooz.notification.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Receiver {
    @Column(name = "receiver_id", nullable = false)
    private Long id;

    @Column(name = "receiver_nickname", nullable = false)
    private String nickname;

}
