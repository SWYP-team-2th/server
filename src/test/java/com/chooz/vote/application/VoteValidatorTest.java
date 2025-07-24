package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.*;
import com.chooz.support.fixture.PostFixture;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    void validateIsVotable() {
        // given
        Post post = PostFixture.createWithId(1L);
        List<Long> pollChoiceIds = List.of(post.getPollChoices().get(0).getId());

        // when & then
        assertDoesNotThrow(() -> voteValidator.validateIsVotable(post, pollChoiceIds));
    }

    @Test
    @DisplayName("validatePost - 이미 마감된 게시글인 경우 예외 발생")
    void validatePost_alreadyClosed() {
        // given
        Post post = PostFixture.createPostBuilder()
                .status(Status.CLOSED)
                .build();
        List<Long> pollChoiceIds = List.of(1L);

        // when & then
        assertThatThrownBy(() -> voteValidator.validateIsVotable(post, pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("validatePost - 마감 시간이 지난 경우 예외 발생")
    void validatePost_closeDateOver() {
        // given
        Post post = PostFixture.createPostBuilder()
                .closeOption(PostFixture.createCloseOptionOverDate())
                .build();
        List<Long> pollChoiceIds = List.of(1L);

        // when & then
        assertThatThrownBy(() -> voteValidator.validateIsVotable(post, pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.CLOSE_DATE_OVER.getMessage());
    }

    @Test
    @DisplayName("validatePost - 투표 참여자 수가 초과한 경우 예외 발생")
    void validatePost_exceedMaxVoterCount() {
        // given
        Post post = PostFixture.createPostBuilder()
                .closeOption(
                        PostFixture.createCloseOptionBuilder()
                                .closeType(CloseType.VOTER)
                                .maxVoterCount(10)
                                .build()
                )
                .build();
        given(voteRepository.countVoterByPostId(post.getId()))
                .willReturn(10L);
        List<Long> pollChoiceIds = List.of(1L);

        // when & then
        assertThatThrownBy(() -> voteValidator.validateIsVotable(post, pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.EXCEED_MAX_VOTER_COUNT.getMessage());
    }

    @Test
    @DisplayName("validateSingleVote - 단일 투표에 여러 선택지 지정 시 예외 발생")
    void validateSingleVote_multipleChoices() {
        // given
        Post post = PostFixture.createPostBuilder()
                .pollOption(
                        PostFixture.createPollOptionBuilder()
                                .pollType(PollType.SINGLE)
                                .build()
                )
                .build();
        List<Long> pollChoiceIds = List.of(1L, 2L);

        // when & then
        assertThatThrownBy(() -> voteValidator.validateIsVotable(post, pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.SINGLE_POLL_ALLOWS_MAXIMUM_ONE_CHOICE.getMessage());
    }

    @Test
    @DisplayName("validateMultipleVotes - 복수 투표에 중복된 선택지 지정 시 예외 발생")
    void validateMultipleVotes_duplicateChoices() {
        // given
        Post post = PostFixture.createPostBuilder()
                .pollOption(
                        PostFixture.createPollOptionBuilder()
                                .pollType(PollType.MULTIPLE)
                                .build()
                )
                .build();
        Long duplicateId = 1L;
        List<Long> pollChoiceIds = List.of(duplicateId, duplicateId); // 중복된 선택지

        // when & then
        assertThatThrownBy(() -> voteValidator.validateIsVotable(post, pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DUPLICATE_POLL_CHOICE.getMessage());
    }

    @Test
    @DisplayName("validatePollChoiceId - 게시물에 없는 선택지 ID 지정 시 예외 발생")
    void validatePollChoiceId_invalidChoiceId() {
        // given
        Post post = PostFixture.createDefaultPost(1L);
        List<Long> pollChoiceIds = List.of(-1L); // 존재하지 않는 선택지 ID

        // when & then
        assertThatThrownBy(() -> voteValidator.validateIsVotable(post, pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_POLL_CHOICE_ID.getMessage());
    }

    @Test
    @DisplayName("validatePollChoiceId - 빈 선택지 리스트일 경우 검증 통과")
    void validatePollChoiceId_emptyList() {
        // given
        Post post = PostFixture.createDefaultPost(1L);
        List<Long> emptyPollChoiceIds = List.of(); // 빈 선택지 리스트

        // when & then
        assertDoesNotThrow(() -> voteValidator.validateIsVotable(post, emptyPollChoiceIds));
    }

    @Test
    @DisplayName("validateVoteStatusAccess - 작성자가 아니고 투표하지 않은 사용자는 투표 현황 조회 불가")
    void validateVoteStatusAccess_notAuthorAndNotVoter() {
        // given
        Long userId = 999L;
        Post post = PostFixture.createDefaultPost(1L); // 작성자 ID: 1L
        List<com.chooz.vote.domain.Vote> votes = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> voteValidator.validateVoteStatusAccess(userId, post, votes))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.ACCESS_DENIED_VOTE_STATUS.getMessage());
    }
}
