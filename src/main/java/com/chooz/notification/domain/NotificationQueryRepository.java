package com.chooz.notification.domain;

import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryRepository {
    Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable);
    Optional<TargetPostDto> findPostByCommentId(Long commentId);
    Optional<TargetUserDto> findUserByCommentId(Long commentId);
    Optional<TargetUserDto> findUserById(Long userId);
    Optional<TargetUserDto> findUserByPostId(Long postId);
    Optional<TargetPostDto> findPostById(Long postId);
    boolean existsByDedupKey(Long ReceiverId, String dedupKey);
    List<TargetUserDto> findVoteUsersByPostId(Long postId);
}
