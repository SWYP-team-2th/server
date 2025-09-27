package com.chooz.notification.application;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.service.NotificationCommandService;
import com.chooz.notification.application.service.NotificationQueryService;
import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.NotificationRepository;
import com.chooz.notification.presentation.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    public CursorBasePaginatedResponse<NotificationResponse> findNotifications(Long userId, Long cursor, int size) {
        return notificationQueryService.findNotifications(userId, cursor, size);
    }
    public boolean existsByDedupKey(Long ReceiverId, String dedupKey) {
        return notificationQueryService.existsByDedupKey(ReceiverId, dedupKey);
    }
    public TargetUserDto findUserByCommentId(Long commentId) {
        return notificationQueryService.findUserByCommentId(commentId);
    }
    public TargetUserDto findUserById(Long userId) {
        return notificationQueryService.findUserById(userId);
    }
    public TargetPostDto findPostByCommentId(Long commentId) {
        return notificationQueryService.findPostByCommentId(commentId);
    }
    public TargetUserDto findUserByPostId(Long postId) {
        return notificationQueryService.findUserByPostId(postId);
    }
    public TargetPostDto findPostById(Long postId) {
        return notificationQueryService.findPostById(postId);
    }
    public Notification create(Notification notification) {
        return notificationCommandService.create(notification);
    }
}
