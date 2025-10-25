package com.chooz.notification.application;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.service.NotificationCommandService;
import com.chooz.notification.application.service.NotificationQueryService;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.TargetType;
import com.chooz.notification.presentation.dto.NotificationPresentResponse;
import com.chooz.notification.presentation.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    public CursorBasePaginatedResponse<NotificationResponse> findNotifications(Long userId, Long cursor, int size) {
        return notificationQueryService.findNotifications(userId, cursor, size);
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
    public void createAll(List<Notification> notifications) {
        notificationCommandService.createAll(notifications);
    }
    public List<TargetUserDto> findVoteUsersByPostId(Long postId) {
        return notificationQueryService.findVoteUsersByPostId(postId);
    }
    public void markRead(Long notificationId) {
        notificationCommandService.markRead(notificationId);
    }
    public NotificationPresentResponse present(Long userId) {
        return notificationQueryService.present(userId);
    }
    public List<Notification> findByTargetIdAndType(Long id, TargetType targetType){
        return notificationQueryService.findByTargetIdAndType(id, targetType);
    }
}
