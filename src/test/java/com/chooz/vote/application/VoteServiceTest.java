package com.chooz.vote.application;

import com.chooz.common.event.EventPublisher;
import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.*;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.persistence.VoteJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class VoteServiceTest extends IntegrationTest {

    @Autowired
    VoteService voteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteJpaRepository voteRepository;

    @Autowired
    PostRepository postRepository;

    @MockitoBean
    EventPublisher eventPublisher;

    @Test
    @DisplayName("단일 투표하기")
    void singleVote() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(
                                PostFixture.createPollOptionBuilder()
                                        .pollType(PollType.SINGLE)
                                        .build())
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();

        // when
        List<Long> voteIds = voteService.vote(user.getId(), post.getId(), List.of(pollChoiceId));

        // then
        Vote vote = voteRepository.findById(voteIds.getFirst()).orElseThrow();
        assertAll(
                () -> assertThat(vote.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPollChoiceId()).isEqualTo(pollChoiceId),
                () -> verify(eventPublisher, times(1)).publish(any(VotedEvent.class))
        );
    }

    @Test
    @DisplayName("단일 투표하기 - 다른 이미지로 투표 변경한 경우")
    void singleVote_change() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(
                                PostFixture.createPollOptionBuilder()
                                        .pollType(PollType.SINGLE)
                                        .build())
                        .build());
        List<PollChoice> pollChoices = post.getPollChoices();
        Long before = pollChoices.get(0).getId();
        Long after = pollChoices.get(1).getId();
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), before));

        // when
        List<Long> voteIds = voteService.vote(user.getId(), post.getId(), List.of(after));

        // then
        Vote vote = voteRepository.findById(voteIds.get(1)).orElseThrow();
        assertThat(vote.getPollChoiceId()).isEqualTo(after);
    }

    @Test
    @DisplayName("단일 투표 - 빈 배열로 투표 시 기존 투표 취소")
    void singleVote_cancelByEmptyChoice() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(PostFixture.createPollOptionBuilder().pollType(PollType.SINGLE).build())
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();
        voteService.vote(user.getId(), post.getId(), List.of(pollChoiceId));

        // when
        voteService.vote(user.getId(), post.getId(), List.of());

        // then
        List<Vote> votes = voteRepository.findByUserIdAndPostIdAndDeletedFalse(user.getId(), post.getId());
        assertThat(votes).isEmpty();
    }

    @Test
    @DisplayName("복수 투표하기 - 여러 선택지 한 번에")
    void multipleVote_atOnce() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(
                                PostFixture.createPollOptionBuilder()
                                        .pollType(PollType.MULTIPLE)
                                        .build())
                        .build()
        );
        List<PollChoice> pollChoices = post.getPollChoices();
        List<Long> choiceIds = List.of(pollChoices.get(0).getId(), pollChoices.get(1).getId());

        // when
        List<Long> voteIds = voteService.vote(user.getId(), post.getId(), choiceIds);

        // then
        List<Vote> votes = voteRepository.findAllByPostIdAndDeletedFalse(post.getId());
        assertThat(votes).hasSize(2);
        assertThat(votes).allMatch(v -> v.getUserId().equals(user.getId()));
        assertThat(votes).extracting(Vote::getPollChoiceId)
                .containsExactlyInAnyOrderElementsOf(choiceIds);
    }

    @Test
    @DisplayName("복수 투표 - 기존 투표와 다른 선택지로 변경")
    void multipleVote_changeChoices() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollChoices(List.of(
                                PostFixture.createPollChoice(),
                                PostFixture.createPollChoice(),
                                PostFixture.createPollChoice())
                        )
                        .pollOption(PostFixture.createPollOptionBuilder()
                                .pollType(PollType.MULTIPLE)
                                .build())
                        .build()
        );
        List<PollChoice> pollChoices = post.getPollChoices();
        Long first = pollChoices.get(0).getId();
        Long second = pollChoices.get(1).getId();
        Long third = pollChoices.get(2).getId();
        voteService.vote(user.getId(), post.getId(), List.of(first, second));

        // when
        List<Long> voteIds = voteService.vote(user.getId(), post.getId(), List.of(second, third));

        // then
        List<Vote> votes = voteRepository.findAllByPostIdAndDeletedFalse(post.getId());
        assertThat(votes).hasSize(2);
        assertThat(votes).allMatch(v -> v.getUserId().equals(user.getId()));
        assertThat(votes).extracting(Vote::getPollChoiceId)
                .containsExactlyInAnyOrder(second, third);
    }

    @Test
    @DisplayName("투표하기 - 투표 마감된 경우")
    void vote_alreadyClosed() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .status(Status.CLOSED)
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();

        // when & then
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), List.of(pollChoiceId)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표하기 - 참여자 초과된 경우")
    void vote_exceedMaxVoterCount() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .closeOption(
                                PostFixture.createCloseOptionBuilder()
                                        .closeType(CloseType.VOTER)
                                        .maxVoterCount(1)
                                        .build())
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), pollChoiceId));

        // when & then
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), List.of(pollChoiceId)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.EXCEED_MAX_VOTER_COUNT.getMessage());
    }

    @Test
    @DisplayName("단일 투표 - 선택지가 여러 개 들어온 경우 예외")
    void singleVote_multipleChoicesException() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(
                                PostFixture.createPollOptionBuilder()
                                        .pollType(PollType.SINGLE)
                                        .build())
                        .build()
        );
        List<Long> pollChoiceIds = post.getPollChoices().stream().map(PollChoice::getId).toList();

        // when & then
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.SINGLE_POLL_ALLOWS_MAXIMUM_ONE_CHOICE.getMessage());
    }

    @Test
    @DisplayName("복수 투표 - 중복된 선택지가 들어온 경우 예외")
    void multipleVote_duplicateChoicesException() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(
                                PostFixture.createPollOptionBuilder()
                                        .pollType(PollType.MULTIPLE)
                                        .build())
                        .build()
        );
        Long pollChoiceId = post.getPollChoices().getFirst().getId();
        List<Long> pollChoiceIds = List.of(pollChoiceId, pollChoiceId); // 중복

        // when & then
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), pollChoiceIds))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DUPLICATE_POLL_CHOICE.getMessage());
    }

    @Test
    @DisplayName("복수 투표 - 모든 선택지 제외 시 전체 투표 취소")
    void multipleVote_cancelAllChoices() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(PostFixture.createPollOptionBuilder().pollType(PollType.MULTIPLE).build())
                        .build()
        );
        List<PollChoice> pollChoices = post.getPollChoices();
        Long first = pollChoices.get(0).getId();
        Long second = pollChoices.get(1).getId();
        voteService.vote(user.getId(), post.getId(), List.of(first, second));

        // when
        voteService.vote(user.getId(), post.getId(), List.of());

        // then
        List<Vote> votes = voteRepository.findByUserIdAndPostIdAndDeletedFalse(user.getId(), post.getId());
        assertThat(votes).isEmpty();
    }

    @Test
    @DisplayName("투표 현황 조회")
    void findVoteResult() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        int voteIndex = 1;
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(voteIndex).getId()));

        //when
        var response = voteService.findVoteResult(user.getId(), post.getId());

        //then
        assertAll(
                () -> assertThat(response).hasSize(2),
                () -> assertThat(response.getFirst().id()).isEqualTo(post.getPollChoices().get(1).getId()),
                () -> assertThat(response.getFirst().title()).isEqualTo(post.getPollChoices().get(1).getTitle()),
                () -> assertThat(response.getFirst().voteCount()).isEqualTo(1),
                () -> assertThat(response.getFirst().voteRatio()).isEqualTo("100"),

                () -> assertThat(response.get(1).id()).isEqualTo(post.getPollChoices().getFirst().getId()),
                () -> assertThat(response.get(1).title()).isEqualTo(post.getPollChoices().getFirst().getTitle()),
                () -> assertThat(response.get(1).voteCount()).isEqualTo(0),
                () -> assertThat(response.get(1).voteRatio()).isEqualTo("0")
        );
    }

    @Test
    @DisplayName("투표 현황 조회 - 중복 투표")
    void findVoteResult_multiple() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createPostBuilder()
                .userId(user.getId())
                .pollOption(PostFixture.multiplePollOption())
                .build());
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(user.getId(), post.getId(), post.getPollChoices().get(1).getId()));

        //when
        var response = voteService.findVoteResult(user.getId(), post.getId());

        //then
        assertAll(
                () -> assertThat(response).hasSize(2),
                () -> assertThat(response.getFirst().id()).isEqualTo(post.getPollChoices().get(0).getId()),
                () -> assertThat(response.getFirst().title()).isEqualTo(post.getPollChoices().get(0).getTitle()),
                () -> assertThat(response.getFirst().voteCount()).isEqualTo(1),
                () -> assertThat(response.getFirst().voteRatio()).isEqualTo("50"),

                () -> assertThat(response.get(1).id()).isEqualTo(post.getPollChoices().get(1).getId()),
                () -> assertThat(response.get(1).title()).isEqualTo(post.getPollChoices().get(1).getTitle()),
                () -> assertThat(response.get(1).voteCount()).isEqualTo(1),
                () -> assertThat(response.get(1).voteRatio()).isEqualTo("50")
        );
    }

    @Test
    @DisplayName("투표 현황 조회 - 투표한 사람인 경우 투표 현황을 조회할 수 있어야 함")
    void findVoteStatus_voteUser() {
        //given
        User author = userRepository.save(UserFixture.createDefaultUser());
        User voter = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(author.getId()));
        voteRepository.save(VoteFixture.createDefaultVote(voter.getId(), post.getId(), post.getPollChoices().getFirst().getId()));

        //when
        var response = voteService.findVoteResult(voter.getId(), post.getId());

        //then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("투표 현황 조회 - 작성자 아니고 투표 안 한 사람인 경우")
    void findVoteResult_notAuthorAndVoter() {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));

        //when
        assertThatThrownBy(() -> voteService.findVoteResult(2L, post.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.ACCESS_DENIED_VOTE_STATUS.getMessage());
    }

}
