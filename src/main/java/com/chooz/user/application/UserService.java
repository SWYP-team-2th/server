package com.chooz.user.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.user.domain.*;
import com.chooz.user.presentation.dto.OnboardingRequest;
import com.chooz.user.presentation.dto.UserInfoResponse;
import com.chooz.user.presentation.dto.UserMyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;
    private final OnboardingStepRepository onboardingStepRepository;

    @Transactional
    public Long createUser(String nickname, String profileImageUrl) {
        User user = userRepository.save(User.create(getOrGenerateNickname(nickname), profileImageUrl));
        return user.getId();
    }

    private String getOrGenerateNickname(String nickname) {
        return Optional.ofNullable(nickname)
                .orElseGet(nicknameGenerator::generate);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.of(user);
    }

    @Transactional(readOnly = true)
    public UserMyInfoResponse findByMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        return UserMyInfoResponse.of(user);
    }

    @Transactional
    public UserInfoResponse completeStep(Long userId, OnboardingRequest onboardingRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        if (user.getOnboardingStep() == null) {
            throw new BadRequestException(ErrorCode.ONBOARDING_NOT_INITIALIZED);
        }
        UpdateOnboardingStep(user, onboardingRequest);
        return UserInfoResponse.of(userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND)));
    }

    private void UpdateOnboardingStep(User user, OnboardingRequest onboardingRequest) {
        onboardingRequest.onboardingStep().entrySet().stream()
                .filter(step -> Boolean.TRUE.equals(step.getValue()))
                .map(Map.Entry::getKey)
                .forEach(stepType -> stepType.apply(user.getOnboardingStep()));
    }
}
