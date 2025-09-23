package com.chooz.notification.persistence;

import com.chooz.notification.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.receiverId = :userId
            AND (:cursor is null OR n.id < :cursor)
        ORDER BY
            n.id DESC
    """)
    Slice<Notification> findByUserId(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
