package com.chooz.auth.application;

import com.chooz.auth.application.jwt.JwtClaim;
import com.chooz.auth.application.jwt.JwtProvider;
import com.chooz.auth.application.jwt.TokenPair;
import com.chooz.support.IntegrationTest;
import com.chooz.user.domain.Role;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JwtProviderIntegrationTest extends IntegrationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @Disabled
    @DisplayName("토큰 생성")
    void create() throws Exception {
        //given


        //when
        TokenPair token = jwtProvider.createToken(new JwtClaim(1L, Role.USER));

        //then
        System.out.println("token.accessToken() = " + token.accessToken());
    }
}
