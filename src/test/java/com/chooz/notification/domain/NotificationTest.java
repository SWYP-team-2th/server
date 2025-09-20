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
        Long receiverId = 1L;
        String receiverNickname = "공개된 츄";
        Long actorId = 2L;
        String actorNickname = "숨겨진 츄";
        String actorProfileUrl = "https://cdn.chooz.site/default_profile.png";
        Long targetId = 3L;
        TargetType targetType = TargetType.COMMENT;
        String targetImageUrl = "https://cdn.chooz.site/default_target.png";
        LocalDateTime eventAt = LocalDateTime.now();
        //when
        Notification notification = Notification.create(
                receiverId,
                receiverNickname,
                actorId,
                actorNickname,
                actorProfileUrl,
                targetId,
                targetType,
                targetImageUrl,
                eventAt
        ).get();

        //then
        assertAll(
                () -> assertThat(notification.getReceiver().getId()).isEqualTo(receiverId),
                () -> assertThat(notification.getReceiver().getNickname()).isEqualTo(receiverNickname),
                () -> assertThat(notification.getActor().getId()).isEqualTo(actorId),
                () -> assertThat(notification.getActor().getNickname()).isEqualTo(actorNickname),
                () -> assertThat(notification.getActor().getProfileUrl()).isEqualTo(actorProfileUrl),
                () -> assertThat(notification.getTarget().getId()).isEqualTo(targetId),
                () -> assertThat(notification.getTarget().getType()).isEqualTo(targetType),
                () -> assertThat(notification.getTarget().getImageUrl()).isEqualTo(targetImageUrl),
                () -> assertThat(notification.getEventAt()).isEqualTo(eventAt)
        );
    }
    @Test
    @DisplayName("알림 읽음 확인")
    void markRead() throws Exception {
        //given
        Long receiverId = 1L;
        String receiverNickname = "공개된 츄";
        Long actorId = 2L;
        String actorNickname = "숨겨진 츄";
        String actorProfileUrl = "https://cdn.chooz.site/default_profile.png";
        Long targetId = 3L;
        TargetType targetType = TargetType.COMMENT;
        String targetImageUrl = "https://cdn.chooz.site/default_target.png";
        LocalDateTime eventAt = LocalDateTime.now();
        //when
        Notification notification = Notification.create(
                receiverId,
                receiverNickname,
                actorId,
                actorNickname,
                actorProfileUrl,
                targetId,
                targetType,
                targetImageUrl,
                eventAt
        ).get();

        notification.markRead();

        //then
        assertAll(
                () -> assertThat(notification.isRead()).isTrue()
        );
    }
}
