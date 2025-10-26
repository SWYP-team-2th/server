package com.chooz.notification.persistence;

import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    boolean existsByReceiverIdAndIsReadFalseAndDeletedFalse(Long userId);
    @Query("""
            SELECT distinct n
            FROM Notification n
            join n.targets t
            where t.id = :targetId
            and t.type = :targetType
            and n.isValid = true
            order by n.id desc
            """
    )
    List<Notification> findByTargetIdAndType(@Param("targetId") Long targetId, @Param("targetType") TargetType targetType);
}
