package com.chooz.notification.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationTest {

    @Test
    @DisplayName("알림 생성")
    void create() throws Exception {
        //given
        Long userId = 1L;
        Long actorId = 2L;
        NotificationType type = NotificationType.COMMENT_LIKED;
        TargetType targetType = TargetType.COMMENT;
        Long targetId = 3L;
        String title = "숨겨진 츄님이 당신의 댓글에 좋아요를 눌렀습니다.";
        String body = "지금 바로 확인해보세요!";
        String thumbUrl = "https://cdn.chooz.site/thumbnail.png";
        String profileImageUrl = "https://cdn.chooz.site/default_profile.png";
        LocalDateTime eventAt = LocalDateTime.now();
        //when
        Notification notification = Notification.create(
                userId,
                actorId,
                type,
                targetType,
                targetId,
                title,
                body,
                thumbUrl,
                profileImageUrl,
                eventAt
        ).get();

        //then
        assertAll(
                () -> assertThat(notification.getUserId()).isEqualTo(userId),
                () -> assertThat(notification.getActorId()).isEqualTo(actorId),
                () -> assertThat(notification.getType()).isEqualTo(type),
                () -> assertThat(notification.getTarget().getId()).isEqualTo(targetId),
                () -> assertThat(notification.getTarget().getType()).isEqualTo(targetType),
                () -> assertThat(notification.getTitle()).isEqualTo(title),
                () -> assertThat(notification.getBody()).isEqualTo(body),
                () -> assertThat(notification.getThumbUrl()).isEqualTo(thumbUrl),
                () -> assertThat(notification.getProfileImageUrl()).isEqualTo(profileImageUrl),
                () -> assertThat(notification.getEventAt()).isEqualTo(eventAt)
        );
    }
    @Test
    @DisplayName("알림 읽음 확인")
    void markRead() throws Exception {
        //given
        Long userId = 1L;
        Long actorId = 2L;
        NotificationType type = NotificationType.COMMENT_LIKED;
        TargetType targetType = TargetType.COMMENT;
        Long targetId = 3L;
        String title = "숨겨진 츄님이 당신의 댓글에 좋아요를 눌렀습니다.";
        String body = "지금 바로 확인해보세요!";
        String thumbUrl = "https://cdn.chooz.site/thumbnail.png";
        String profileImageUrl = "https://cdn.chooz.site/default_profile.png";
        LocalDateTime eventAt = LocalDateTime.now();
        //when
        Notification notification = Notification.create(
                userId,
                actorId,
                type,
                targetType,
                targetId,
                title,
                body,
                thumbUrl,
                profileImageUrl,
                eventAt
        ).get();

        notification.markRead();

        //then
        assertAll(
                () -> assertThat(notification.isRead()).isTrue()
        );
    }
}
