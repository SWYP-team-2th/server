package com.chooz.notification.persistence;

import com.chooz.notification.application.dto.NotificationDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationQueryDslRepository notificationQueryDslRepository;

//    @Override
//    public Slice<Notification> findNotifications(Long userId, Long cursor, Pageable pageable) {
//        return notificationJpaRepository.findByUserId(userId, cursor, pageable);
//    }

    @Override
    public Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable) {
        return notificationQueryDslRepository.findNotifications(userId, cursor, pageable);
    }

    @Override
    public Optional<TargetPostDto> getPost(Long commentId) {
        return notificationQueryDslRepository.getPost(commentId);
    }

    @Override
    public Optional<TargetUserDto> getUserByCommentId(Long commentId) {
        return notificationQueryDslRepository.getUserByCommentId(commentId);
    }

    @Override
    public Optional<TargetUserDto> getUser(Long userId) {
        return notificationQueryDslRepository.getUser(userId);
    }
}