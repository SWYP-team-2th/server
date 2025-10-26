package com.chooz.notification.domain;

import com.chooz.notification.application.dto.NotificationContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationTest {

    @Test
    @DisplayName("알림 생성")
    void create() throws Exception {
        //given
        Long receiverId = 1L;
        Long actorId = 2L;
        String title = "숨겨진 츄님이 좋아요를 눌렀어요!";
        String content = "지금 바로 확인해보세요.";
        String profileUrl =  "https://cdn.chooz.site/default_profile.png";
        List<Target> targets = List.of(Target.of(3L, TargetType.POST));
        String imageUrl = "https://cdn.chooz.site/images/20865b3c-4e2c-454a-81a1-9ca31bbaf77d";
        LocalDateTime eventAt = LocalDateTime.now();
        NotificationType notificationType = NotificationType.COMMENT_LIKED;

        Notification notification = Notification.create(
                notificationType,
                eventAt,
                NotificationContent.of(
                        receiverId,
                        actorId,
                        title,
                        content,
                        profileUrl,
                        imageUrl,
                        targets
                )
        ).get();

        //when then
        assertAll(
                () -> assertThat(notification.getReceiverId()).isEqualTo(receiverId),
                () -> assertThat(notification.getProfileUrl()).isEqualTo(profileUrl),
                () -> assertThat(notification.getTitle()).isEqualTo(title),
                () -> assertThat(notification.getContent()).isEqualTo(content),
                () -> assertThat(notification.getTargets())
                        .allSatisfy(target -> {
                                    assertThat(target.getId()).isEqualTo(3L);
                                    assertThat(target.getType()).isEqualTo(TargetType.POST);
                                }
                        ),
                () -> assertThat(notification.getImageUrl()).isEqualTo(imageUrl),
                () -> assertThat(notification.isRead()).isEqualTo(false),
                () -> assertThat(notification.getEventAt()).isEqualTo(eventAt)
        );
    }
    @Test
    @DisplayName("알림 읽음 확인")
    void markRead() throws Exception {
        //given
        Long receiverId = 1L;
        Long actorId = 2L;
        String title = "숨겨진 츄님이 좋아요를 눌렀어요!";
        String content = "지금 바로 확인해보세요.";
        String profileUrl =  "https://cdn.chooz.site/default_profile.png";
        List<Target> targets = List.of(Target.of(3L, TargetType.POST));
        String imageUrl = "https://cdn.chooz.site/images/20865b3c-4e2c-454a-81a1-9ca31bbaf77d";
        LocalDateTime eventAt = LocalDateTime.now();
        NotificationType notificationType = NotificationType.COMMENT_LIKED;

        Notification notification = Notification.create(
                notificationType,
                eventAt,
                NotificationContent.of(
                        receiverId,
                        actorId,
                        title,
                        content,
                        profileUrl,
                        imageUrl,
                        targets
                )
        ).get();

        //when
        notification.markRead();

        //then
        assertAll(
                () -> assertThat(notification.isRead()).isTrue()
        );
    }
}
