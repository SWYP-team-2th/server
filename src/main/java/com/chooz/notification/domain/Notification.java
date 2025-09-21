package com.chooz.notification.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Receiver receiver;

    @Embedded
    private Actor actor;

    @Embedded
    private Target target;

    @Column(name = "is_valid", nullable = false)
    private boolean isValid;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "event_at", nullable = false)
    private LocalDateTime eventAt;

    public static Optional<Notification> create(
            Long receiverId,
            String receiverNickname,
            Long actorId,
            String actorNickname,
            String actorProfileUrl,
            Long targetId,
            TargetType targetType,
            String targetImageUrl,
            LocalDateTime eventAt
    ) {
        if (checkMine(actorId, receiverId)) {
            return Optional.empty();
        }
        return Optional.of(Notification.builder()
                .receiver(new Receiver(receiverId, receiverNickname))
                .actor(new Actor(actorId, actorNickname, actorProfileUrl))
                .target(new Target(targetId, targetType, targetImageUrl))
                .isValid(true)
                .isRead(false)
                .eventAt(eventAt)
                .build());
    }
    private static boolean checkMine(Long actorId, Long receiverId) {
        return actorId != null && actorId.equals(receiverId);
    }

    public void markRead() {
        if (!isRead) {
            this.isRead = true;
        }
    }
    public void invalidate() {
        if (isValid) {
            this.isValid = false;
        }
    }
}
