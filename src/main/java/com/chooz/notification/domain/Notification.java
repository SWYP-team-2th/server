package com.chooz.notification.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.notification.application.dto.NotificationContent;
import com.chooz.notification.domain.event.CommentLikedNotificationEvent;
import com.chooz.post.domain.CloseType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Embedded
    private Actor actor;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "notification_targets",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    private List<Target> targets = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "dedupKey", nullable = false)
    private String dedupKey;

    @Column(name = "is_valid", nullable = false)
    private boolean isValid;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "event_at", nullable = false)
    private LocalDateTime eventAt;

    public static Optional<Notification> create(
            NotificationType notificationType,
            LocalDateTime eventAt,
            NotificationContent notificationContent
    ) {
        if (checkMine(notificationContent.actorId(), notificationContent.receiverId())) {
            return Optional.empty();
        }
//        if(checkMySelfClosePost(notificationType, closeType)){
//            return Optional.empty();
//        }
        return Optional.of(Notification.builder()
                .receiverId(notificationContent.receiverId())
                .actor(Actor.of(
                        notificationContent.actorId(),
                        notificationContent.actorNickname(),
                        notificationContent.actorProfileUrl())
                )
                .targets(List.copyOf(notificationContent.targets()))
                .notificationType(notificationType)
                .imageUrl(notificationContent.imageUrl())
                .dedupKey(makeDedupKey(notificationType, notificationContent.actorId(), notificationContent.targets()))
                .isValid(true)
                .isRead(false)
                .eventAt(eventAt)
                .build());
    }
    private static boolean checkMine(Long actorId, Long receiverId) {
        return actorId != null && actorId.equals(receiverId);
    }
//    private static boolean checkMySelfClosePost(NotificationType notificationType, CloseType closeType) {
//        return notificationType == NotificationType.MY_POST_CLOSED && closeType == CloseType.SELF;
//    }
    public static String makeDedupKey(NotificationType notificationType, Long actorId, List<Target> targets) {
        StringBuilder key = new StringBuilder(100)
                .append(actorId).append('|')
                .append(notificationType.name());
        targets = targets.stream().sorted(Comparator.comparing(Target::getType)).toList();
        for (Target target : targets) {
            key.append('|').append(target.getType()).append(':').append(target.getId());
        }
        return key.toString();
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
