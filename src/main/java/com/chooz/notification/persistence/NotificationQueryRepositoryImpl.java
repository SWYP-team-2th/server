package com.chooz.notification.persistence;

import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.domain.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final NotificationQueryDslRepository notificationQueryDslRepository;

    @Override
    public Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable) {
        return notificationQueryDslRepository.findNotifications(userId, cursor, pageable);
    }

    @Override
    public Optional<TargetPostDto> findPostByCommentId(Long commentId) {
        return notificationQueryDslRepository.findPostByCommentId(commentId);
    }

    @Override
    public Optional<TargetUserDto> findUserByCommentId(Long commentId) {
        return notificationQueryDslRepository.findUserByCommentId(commentId);
    }

    @Override
    public Optional<TargetUserDto> findUserById(Long userId) {
        return notificationQueryDslRepository.findUserById(userId);
    }

    @Override
    public Optional<TargetUserDto> findUserByPostId(Long postId) {
        return notificationQueryDslRepository.findUserByPostId(postId);
    }

    @Override
    public Optional<TargetPostDto> findPostById(Long postId) {
        return notificationQueryDslRepository.findPostById(postId);
    }

    @Override
    public boolean existsByDedupKey(Long ReceiverId, String dedupKey) {
        return notificationQueryDslRepository.existsByDedupKey(ReceiverId, dedupKey);
    }

    @Override
    public List<TargetUserDto> findVoteUsersByPostId(Long postId) {
        return notificationQueryDslRepository.findVoteUsersByPostId(postId);
    }
}