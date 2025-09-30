package com.chooz.notification.application.service;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.domain.NotificationRepository;
import com.chooz.notification.presentation.dto.NotificationPresentResponse;
import com.chooz.notification.presentation.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationRepository notificationRepository;

    public CursorBasePaginatedResponse<NotificationResponse> findNotifications(Long userId, Long cursor, int size) {
        Slice<NotificationDto> notifications = notificationQueryRepository.findNotifications(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(notifications.map(NotificationResponse::of));
    }
    public TargetUserDto findUserByCommentId(Long commentId) {
        return notificationQueryRepository.findUserByCommentId(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }
    public TargetUserDto findUserById(Long userId) {
        return notificationQueryRepository.findUserById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }
    public TargetPostDto findPostByCommentId(Long commentId) {
        return notificationQueryRepository.findPostByCommentId(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
    }
    public TargetUserDto findUserByPostId(Long postId) {
        return notificationQueryRepository.findUserByPostId(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }
    public TargetPostDto findPostById(Long postId) {
        return notificationQueryRepository.findPostById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
    }
    public List<TargetUserDto> findVoteUsersByPostId(Long postId) {
        return notificationQueryRepository.findVoteUsersByPostId(postId);
    }
    public NotificationPresentResponse present(Long userId) {
        return NotificationPresentResponse.of(notificationRepository.existsByReceiverIdAndIsReadFalseAndDeletedFalse(userId));
    }

}
