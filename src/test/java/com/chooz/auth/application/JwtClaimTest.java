package com.chooz.auth.application;

import com.chooz.auth.application.jwt.JwtClaim;
import com.chooz.user.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtClaimTest {

    @Test
    @DisplayName("JwtClaim 생성")
    void idAsLong() {
        // given
        long givenId = 1;
        Role givenRole = Role.USER;

        // when
        JwtClaim jwtClaim = JwtClaim.from(givenId, givenRole);

        // then
        assertThat(jwtClaim.idAsLong()).isEqualTo(givenId);
        assertThat(jwtClaim.role()).isEqualTo(givenRole);
    }
}
