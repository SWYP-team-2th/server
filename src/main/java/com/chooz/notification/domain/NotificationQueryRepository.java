package com.chooz.notification.domain;

import com.chooz.notification.application.dto.NotificationDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface NotificationQueryRepository {
    Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable);
    Optional<TargetPostDto> getPostByCommentId(Long commentId);
    Optional<TargetUserDto> getUserByCommentId(Long commentId);
    Optional<TargetUserDto> getUserById(Long userId);
    Optional<TargetUserDto> getUserByPostId(Long postId);
    Optional<TargetPostDto> getPostById(Long postId);
}
