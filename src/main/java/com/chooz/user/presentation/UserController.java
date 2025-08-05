package com.chooz.user.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.user.application.UserService;
import com.chooz.user.presentation.dto.OnboardingRequest;
import com.chooz.user.presentation.dto.UserInfoResponse;
import com.chooz.user.presentation.dto.UserMyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> findUserInfo(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMyInfoResponse> findMyInfo(
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(userService.findByMe(userInfo.userId()));
    }

    @PatchMapping("/onboarding")
    public ResponseEntity<UserInfoResponse> findUserInfo(
            @RequestBody OnboardingRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(userService.completeStep(userInfo.userId(), request));
    }
}
