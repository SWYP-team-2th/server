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
public class Receiver {
    @Column(name = "receiver_id", nullable = false)
    private Long id;
    public static Receiver of(Long id){
        return new Receiver(id);
    }
}
