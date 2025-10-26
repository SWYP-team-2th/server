package com.chooz.auth.presentation.dto;

public record AuthResponse(String accessToken, Long userId, com.chooz.user.domain.Role role) {
}
