package com.chooz.notification.domain;

import com.chooz.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "actor_id")
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Embedded
    private Target target;

    @Column(name = "title")
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "thumb_url", length = 255)
    private String thumbUrl;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "event_at", nullable = false)
    private LocalDateTime eventAt;

    public static Optional<Notification> create(
            Long userId,
            Long actorId,
            NotificationType type,
            TargetType targetType,
            Long targetId,
            String title,
            String body,
            String thumbUrl,
            String profileImageUrl,
            LocalDateTime eventAt
    ) {
        if (checkMine(actorId, userId)) {
            return Optional.empty();
        }
        return Optional.of(Notification.builder()
                .userId(userId)
                .actorId(actorId)
                .type(type)
                .target(new Target(targetType, targetId))
                .title(title)
                .body(body)
                .thumbUrl(thumbUrl)
                .profileImageUrl(profileImageUrl)
                .isRead(false)
                .eventAt(eventAt)
                .build());
    }
    private static boolean checkMine(Long actorId, Long userId) {
        return actorId != null && actorId.equals(userId);
    }

    public void markRead() {
        if (!isRead) {
            this.isRead = true;
        }
    }
}
