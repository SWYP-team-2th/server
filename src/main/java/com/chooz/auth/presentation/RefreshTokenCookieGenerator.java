package com.chooz.auth.presentation;

import com.chooz.common.presentation.CustomHeader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieGenerator {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie(CustomHeader.CustomCookie.REFRESH_TOKEN, refreshToken);
        cookie.setHttpOnly(true);
        if ("local".equals(activeProfile)) {
            cookie.setSecure(false);
        } else {
            cookie.setSecure(true);
            cookie.setAttribute("SameSite", "None");
        }
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        return cookie;
    }

    public void removeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(CustomHeader.CustomCookie.REFRESH_TOKEN, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        if ("local".equals(activeProfile)) {
            cookie.setSecure(false);
        } else {
            cookie.setSecure(true);
            cookie.setAttribute("SameSite", "None");
        }
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
