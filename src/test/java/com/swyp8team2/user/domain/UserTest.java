package com.swyp8team2.user.domain;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    @DisplayName("user Entity 생성")
    void create() throws Exception {
        //given
        String nickname = "nickname";

        //when
        User user = User.create(nickname, "email");

        //then
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("user Entity 생성 - 파라미터가 null인 경우")
    void create_null() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> User.create(null, "email"))
                .isInstanceOf(InternalServerException.class)
                .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage());
    }

    @Test
    @DisplayName("user Entity 생성 - nickname이 빈 문자인 경우")
    void create_emptyString() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> User.create("", "email"))
                .isInstanceOf(InternalServerException.class)
                .hasMessage(ErrorCode.INVALID_INPUT_VALUE.getMessage());
    }
}
