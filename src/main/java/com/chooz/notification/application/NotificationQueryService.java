package com.chooz.notification.application;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.notification.domain.Notification;
import com.chooz.notification.domain.NotificationQueryRepository;
import com.chooz.notification.presentation.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;

    public CursorBasePaginatedResponse<NotificationResponse> findNotifications(Long userId, Long cursor, int size) {
        Slice<Notification> notificationSlice = notificationQueryRepository.findNotifications(userId, cursor, PageRequest.ofSize(size));
        return CursorBasePaginatedResponse.of(notificationSlice.map(NotificationResponse::of));
    }
}
