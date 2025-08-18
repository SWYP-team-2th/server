package com.chooz.user.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.user.application.UserService;
import com.chooz.user.presentation.dto.OnboardingRequest;
import com.chooz.user.presentation.dto.UpdateUserRequest;
import com.chooz.user.presentation.dto.UserInfoResponse;
import com.chooz.user.presentation.dto.UserMyInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PutMapping("/me/profile")
    public ResponseEntity<Void> updateMyInfo(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
            ) {
        userService.updateUser(userInfo.userId(), updateUserRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/onboarding")
    public ResponseEntity<UserInfoResponse> findUserInfo(
            @Valid @RequestBody OnboardingRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(userService.completeStep(userInfo.userId(), request));
    }
}
