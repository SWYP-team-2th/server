package com.chooz.user.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.user.domain.*;
import com.chooz.user.presentation.dto.UserInfoResponse;
import com.chooz.user.presentation.dto.UserMyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;
    private final UserOnboardingStepRepository userOnboardingStepRepository;

    @Transactional
    public Long createUser(String nickname, String profileImageUrl) {
        User user = userRepository.save(User.create(getNickname(nickname), getProfileImage(profileImageUrl), List.of()));
        return user.getId();
    }

    private String getNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .orElseGet(() -> nicknameGenerator.generate());
    }

    private String getProfileImage(String profileImageUrl) {
        return Optional.ofNullable(profileImageUrl)
                .orElse(User.DEFAULT_PROFILE_URL);
    }

    public UserInfoResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.of(user);
    }

    public UserMyInfoResponse findByMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserMyInfoResponse.of(user);
    }

    public UserInfoResponse findByIdFetchOnboardingSteps(Long userId) {
        User user = userRepository.findByIdFetchOnboardingSteps(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.of(user);
    }

    public UserMyInfoResponse findByMeFetchOnboardingSteps(Long userId) {
        User user = userRepository.findByIdFetchOnboardingSteps(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserMyInfoResponse.of(user);
    }

    @Transactional
    public UserMyInfoResponse completeStep(Long userId, OnboardingStep step) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        if(!userOnboardingStepRepository.existsByUserAndStep(user, step)) {
            userOnboardingStepRepository.save(UserOnboardingStep.create(user, step));
        }
        return UserMyInfoResponse.of(userRepository.findByIdFetchOnboardingSteps(user.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND)));
    }
}
