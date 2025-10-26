package com.chooz.support;

import com.chooz.support.security.TestSecurityContextFactory;
import com.chooz.user.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactory.class)
public @interface WithMockUserInfo {
    long userId() default 1L;
    Role role() default Role.USER;
}
