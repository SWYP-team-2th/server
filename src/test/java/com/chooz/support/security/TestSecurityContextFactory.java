package com.chooz.support.security;

import com.chooz.auth.domain.UserInfo;
import com.chooz.support.WithMockUserInfo;
import com.chooz.user.domain.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

public class TestSecurityContextFactory implements WithSecurityContextFactory<WithMockUserInfo> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserInfo annotation) {
        long userId = annotation.userId();
        UserInfo userInfo = new UserInfo(userId, Role.USER);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userInfo,
                null,
                Collections.emptyList()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
        return context;
    }
}
