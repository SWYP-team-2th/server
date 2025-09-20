package com.chooz.notification.persistence;

import com.chooz.notification.application.dto.NotificationDto;
import com.chooz.notification.application.dto.QNotificationDto;
import com.chooz.notification.application.dto.QTargetPostDto;
import com.chooz.notification.application.dto.QTargetUserDto;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.domain.TargetType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.chooz.comment.domain.QComment.comment;
import static com.chooz.notification.domain.QNotification.notification;
import static com.chooz.post.domain.QPost.post;
import static com.chooz.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class NotificationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<NotificationDto> findNotifications(Long userId, Long cursor, Pageable pageable) {
        List<NotificationDto> notifications = queryFactory
                .select(new QNotificationDto(
                        notification.id,
                        post.id,
                        notification.receiver.id,
                        notification.receiver.nickname,
                        notification.actor.id,
                        notification.actor.nickname,
                        notification.actor.profileUrl,
                        notification.target.id,
                        notification.target.type,
                        notification.target.imageUrl,
                        notification.isRead,
                        notification.eventAt
                        )
                )
                .from(notification)
                .leftJoin(comment)
                .on(notification.target.type.eq(TargetType.COMMENT)
                        .and(comment.id.eq(notification.target.id)))
                .leftJoin(post)
                .on(post.id.eq(comment.postId))
                .where(
                        notification.receiver.id.eq(userId),
                        cursor != null ? notification.id.lt(cursor) : null
                )
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = notifications.size() > pageable.getPageSize();
        if (hasNext) notifications.removeLast();
        return new SliceImpl<>(notifications, pageable, hasNext);
    }

    Optional<TargetPostDto> getPost(Long commentId) {
         return Optional.ofNullable(
                 queryFactory.select(new QTargetPostDto(post.id, post.imageUrl))
                         .from(comment)
                         .join(post).on(post.id.eq(comment.postId))
                         .where(comment.id.eq(commentId))
                         .limit(1)
                         .fetchFirst());
    }
    Optional<TargetUserDto> getUserByCommentId(Long commentId) {
        return Optional.ofNullable(
                queryFactory.select(new QTargetUserDto(user.id, user.nickname, user.profileUrl))
                        .from(comment)
                        .join(user).on(user.id.eq(comment.userId))
                        .where(comment.id.eq(commentId))
                        .limit(1)
                        .fetchFirst());
    }
    Optional<TargetUserDto> getUser(Long userId) {
        return Optional.ofNullable(
                queryFactory.select(new QTargetUserDto(user.id, user.nickname, user.profileUrl))
                        .from(user)
                        .where(user.id.eq(userId))
                        .limit(1)
                        .fetchFirst());
    }

}
