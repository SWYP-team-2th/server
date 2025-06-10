package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.Status;
import com.chooz.support.fixture.PostFixture;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VoteValidatorTest {

    @InjectMocks
    VoteValidator voteValidator;

    @Mock
    VoteRepository voteRepository;

    @Mock
    Clock clock;

    @Test
    @DisplayName("투표 유효성 검사")
    void validateIsVotablePost() throws Exception {
        //given
        Post post = PostFixture.createDefaultPost(1L);

        //when then
        assertDoesNotThrow(() -> voteValidator.validateIsVotablePost(post));
    }

    @Test
    @DisplayName("투표 유효성 검사 - 이미 마감된 게시글인 경우 예외 발생")
    void validateIsVotablePost_alreadyClosedPost() throws Exception {
        //given
        Post post = PostFixture.createPostBuilder()
                .status(Status.CLOSED)
                .build();

        //when then
        assertThatThrownBy(() -> voteValidator.validateIsVotablePost(post))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 유효성 검사 - 마감 시간이 지난 경우 예외 발생")
    void validateIsVotablePost_closeDateOver() throws Exception {
        //given
        Post post = PostFixture.createPostBuilder()
                .closeOption(
                        PostFixture.createCloseOptionBuilder()
                                .closeType(CloseType.DATE)
                                .closedAt(LocalDateTime.now())
                                .build()
                )
                .build();

        //when then
        assertThatThrownBy(() -> voteValidator.validateIsVotablePost(post))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.CLOSE_DATE_OVER.getMessage());
    }

    @Test
    @DisplayName("투표 유효성 검사 - 투표 참여자 수가 초과한 경우 예외 발생")
    void validateIsVotablePost_exceedMaxVoterCount() throws Exception {
        //given
        Post post = PostFixture.createPostBuilder()
                .closeOption(
                        PostFixture.createCloseOptionBuilder()
                                .closeType(CloseType.VOTER)
                                .maxVoterCount(1)
                                .build()
                )
                .build();
        given(voteRepository.countVoterByPostId(post.getId()))
                .willReturn(1L);

        //when then
        assertThatThrownBy(() -> voteValidator.validateIsVotablePost(post))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.EXCEED_MAX_VOTER_COUNT.getMessage());
    }
}
