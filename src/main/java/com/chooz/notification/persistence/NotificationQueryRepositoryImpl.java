package com.chooz.notification.persistence;

import com.chooz.notification.application.dto.NotificationDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationQueryDslRepository notificationQueryDslRepository;

    @Override
    public Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable) {
        return notificationQueryDslRepository.findNotifications(userId, cursor, pageable);
    }

    @Override
    public Optional<TargetPostDto> getPostByCommentId(Long commentId) {
        return notificationQueryDslRepository.getPostByCommentId(commentId);
    }

    @Override
    public Optional<TargetUserDto> getUserByCommentId(Long commentId) {
        return notificationQueryDslRepository.getUserByCommentId(commentId);
    }

    @Override
    public Optional<TargetUserDto> getUserById(Long userId) {
        return notificationQueryDslRepository.getUser(userId);
    }

    @Override
    public Optional<TargetUserDto> getUserByPostId(Long postId) {
        return notificationQueryDslRepository.getUserByPostId(postId);
    }

    @Override
    public Optional<TargetPostDto> getPostById(Long postId) {
        return notificationQueryDslRepository.getPostById(postId);
    }
}