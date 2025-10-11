package com.chooz.notification.application;

import com.chooz.notification.application.dto.NotificationContent;
import com.chooz.notification.application.service.NotificationCommandService;
import com.chooz.notification.application.service.NotificationQueryService;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Target;
import com.chooz.notification.domain.TargetType;
import com.chooz.notification.persistence.NotificationJpaRepository;
import com.chooz.notification.presentation.dto.NotificationPresentResponse;
import com.chooz.notification.presentation.dto.NotificationResponse;
import com.chooz.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NotificationQueryServiceTest extends IntegrationTest {

    @Autowired
    NotificationQueryService notificationQueryService;

    @Autowired
    NotificationCommandService notificationCommandService;

    @Autowired
    NotificationJpaRepository notificationJpaRepository;

    @AfterTransaction
    void clean() {
        notificationJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("알림 조회")
    void notifications() throws Exception {
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
        notificationCommandService.create(notification);
        List<NotificationResponse> notifications = notificationQueryService.findNotifications(
                receiverId,
                null,
                10
        ).data();

        //then
        assertAll(
                () -> assertThat(notifications.size()).isOne(),
                () -> assertThat(notifications.getFirst().content()).isEqualTo(content),
                () -> assertThat(notifications.getFirst().title()).isEqualTo(title),
                () -> assertThat(notifications.getFirst().profileUrl()).isEqualTo(profileUrl),
                () -> assertThat(notifications.getFirst().imageUrl()).isEqualTo(imageUrl),
                () -> assertThat(notifications.getFirst().isRead()).isEqualTo(false),
                () -> assertThat(notifications.getFirst().isValid()).isEqualTo(true)

        );
    }
    @Test
    @DisplayName("알림 상태 확인")
    void present() throws Exception {
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
        notificationCommandService.create(notification);
        NotificationPresentResponse notificationPresentResponse = notificationQueryService.present(receiverId);
        //then
        assertAll(
                () -> assertThat(notificationPresentResponse.present()).isTrue()
        );
    }
}
