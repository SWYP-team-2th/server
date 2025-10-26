package com.chooz.post.domain;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.chooz.post.domain.CloseType.DATE;
import static com.chooz.post.domain.CloseType.SELF;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CloseOptionTest {

    @Test
    @DisplayName("마감 옵션 생성")
    void create() throws Exception {
        assertDoesNotThrow(() -> CloseOption.create(SELF, null, null));
        assertDoesNotThrow(() -> CloseOption.create(CloseType.DATE, LocalDateTime.now().plusDays(1), null));
        assertDoesNotThrow(() -> CloseOption.create(CloseType.VOTER, null, 5));
    }

    @Test
    @DisplayName("시간 마감 옵션 생성 실패 - 마감시간이 1시간 이내인 경우")
    void createDateCloseOptionException() throws Exception {
        assertThatThrownBy(() -> CloseOption.create(DATE, LocalDateTime.now().plusMinutes(59), null))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_DATE_CLOSE_OPTION.getMessage());
    }

    @Test
    @DisplayName("투표자 마감 옵션 생성 실패 - 투표자 수가 1 미만 또는 999 초과인 경우")
    void createVoterCloseOptionException() throws Exception {
        assertThatThrownBy(() -> CloseOption.create(CloseType.VOTER, null, 0))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_VOTER_CLOSE_OPTION.getMessage());
        assertThatThrownBy(() -> CloseOption.create(CloseType.VOTER, null, 1000))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_VOTER_CLOSE_OPTION.getMessage());
    }
}
