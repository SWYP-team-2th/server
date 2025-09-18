package com.chooz.notification.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.notification.application.NotificationQueryService;
import com.chooz.notification.presentation.dto.NotificationResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationQueryService notificationQueryService;

    @GetMapping("")
    public ResponseEntity<CursorBasePaginatedResponse<NotificationResponse>> findNotifications(
            @RequestParam(name = "cursor", required = false) @Min(0) Long cursor,
            @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(notificationQueryService.findNotifications(userInfo.userId(), cursor, size));
    }
}
