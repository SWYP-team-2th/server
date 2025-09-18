package com.chooz.notification.infrastructure.adapter;

import com.chooz.notification.application.port.view.UserView;
import com.chooz.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

interface UserViewRepository extends Repository<User, Long> {
    @Query("""
            select new com.chooz.notification.application.port.view.UserView(u.id, u.nickname, u.profileUrl)
            from Comment c
            join User u on u.id = c.userId
            where c.id = :commentId
    """)
    Optional<UserView> findViewByCommentId(Long commentId);

    @Query("""
            select new com.chooz.notification.application.port.view.UserView(u.id, u.nickname, u.profileUrl)
            from User u
            where u.id = :userId
    """)
    Optional<UserView> findViewById(Long userId);
}
