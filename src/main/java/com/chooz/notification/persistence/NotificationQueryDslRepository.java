package com.chooz.notification.persistence;

import com.chooz.notification.application.dto.QTargetPostDto;
import com.chooz.notification.application.dto.QTargetUserDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.web.dto.NotificationDto;
import com.chooz.notification.application.web.dto.NotificationRowDto;
import com.chooz.notification.application.web.dto.QNotificationRowDto;
import com.chooz.notification.application.web.dto.QTargetDto;
import com.chooz.notification.application.web.dto.TargetDto;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.QTarget;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.chooz.comment.domain.QComment.comment;
import static com.chooz.notification.domain.QNotification.notification;
import static com.chooz.post.domain.QPost.post;
import static com.chooz.user.domain.QUser.user;
import static com.chooz.vote.domain.QVote.vote;

@Repository
@RequiredArgsConstructor
public class NotificationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable) {
        List<NotificationRowDto> notificationRows = queryFactory
                .select(new QNotificationRowDto(
                        notification.id,
                        notification.notificationType,
                        notification.profileUrl,
                        notification.title,
                        notification.content,
                        notification.imageUrl,
                        notification.isRead,
                        notification.eventAt
                        )
                )
                .from(notification)
                .where(
                        notification.receiverId.eq(userId),
                        notification.isValid.eq(true),
                        cursor != null ? notification.id.lt(cursor) : null
                )
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        if(notificationRows.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }
        List<NotificationDto> notifications = findNotificationsWithTarget(notificationRows);

        boolean hasNext = notifications.size() > pageable.getPageSize();
        if (hasNext) notifications.removeLast();
        return new SliceImpl<>(notifications, pageable, hasNext);
    }
    private List<NotificationDto> findNotificationsWithTarget(List<NotificationRowDto> notificationRows) {
        QTarget target = QTarget.target;
        List<Long> ids = notificationRows.stream().map(NotificationRowDto::id).toList();
        Map<Long, List<TargetDto>> targetsByNotificationId = queryFactory
                .from(notification)
                .join(notification.targets, target)
                .where(notification.id.in(ids))
                .transform(com.querydsl.core.group.GroupBy.groupBy(notification.id).as(
                        com.querydsl.core.group.GroupBy.list(new QTargetDto(target.id, target.type))
                ));
        return notificationRows.stream().map(
                row -> new NotificationDto(
                        row,
                        targetsByNotificationId.getOrDefault(row.id(), List.of())
                )).toList();
    }

    public Optional<TargetPostDto> findPostByCommentId(Long commentId) {
         return Optional.ofNullable(
                 queryFactory.select(new QTargetPostDto(post.id, post.title, post.imageUrl))
                         .from(comment)
                         .join(post).on(post.id.eq(comment.postId), post.deleted.eq(false))
                         .where(comment.id.eq(commentId))
                         .limit(1)
                         .fetchFirst());
    }
    public Optional<TargetUserDto> findUserByCommentId(Long commentId) {
        return Optional.ofNullable(
                queryFactory.select(new QTargetUserDto(user.id, user.nickname, user.profileUrl))
                        .from(comment)
                        .join(user).on(user.id.eq(comment.userId))
                        .where(comment.id.eq(commentId))
                        .limit(1)
                        .fetchFirst());
    }
    public Optional<TargetUserDto> findUserById(Long userId) {
        return Optional.ofNullable(
                queryFactory.select(new QTargetUserDto(user.id, user.nickname, user.profileUrl))
                        .from(user)
                        .where(user.id.eq(userId))
                        .limit(1)
                        .fetchFirst());
    }
    public Optional<TargetUserDto> findUserByPostId(Long postId) {
        return Optional.ofNullable(
                queryFactory.select(new QTargetUserDto(user.id, user.nickname, user.profileUrl))
                        .from(user)
                        .join(post).on(user.id.eq(post.userId), post.deleted.eq(false))
                        .where(post.id.eq(postId))
                        .limit(1)
                        .fetchFirst());
    }
    public Optional<TargetPostDto> findPostById(Long postId) {
        return Optional.ofNullable(
                queryFactory.select(new QTargetPostDto(post.id, post.title, post.imageUrl))
                        .from(post)
                        .where(
                                post.id.eq(postId),
                                post.deleted.eq(false)
                        )
                        .limit(1)
                        .fetchFirst());
    }
    public boolean existsByDedupKey(Long receiverId, String dedupkey) {
        Integer one = queryFactory.selectOne()
                        .from(notification)
                        .where(
                                notification.receiverId.eq(receiverId),
                                notification.dedupKey.eq(dedupkey)
                        ).fetchFirst();
        return one != null;
    }
    public List<Notification> existsDedupKeyByNotifications(List<Notification> notifications) {
        BooleanBuilder builder = new BooleanBuilder();
        for (Notification n : notifications) {
            builder.or(
                    notification.receiverId.eq(n.getReceiverId())
                            .and(notification.dedupKey.eq(n.getDedupKey()))
            );
        }
        return queryFactory.selectFrom(notification)
                .where(builder)
                .fetch();
    }
    public List<TargetUserDto> findVoteUsersByPostId(Long postId) {
        return queryFactory.select(new QTargetUserDto(user.id, user.nickname, user.profileUrl))
                .from(user)
                .join(vote).on(user.id.eq(vote.userId), vote.deleted.eq(false))
                .join(post).on(post.id.eq(vote.postId), post.deleted.eq(false))
                .fetch();
    }

}
