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
import com.chooz.vote.domain.VoteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
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
    VoteRepository voteRepository;

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

        // when
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());

        // then
        Vote vote = voteRepository.findById(voteId).get();
        assertAll(
                () -> assertThat(vote.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPollChoiceId()).isEqualTo(post.getPollChoices().get(0).getId()),
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
        Long voteId = voteService.vote(user.getId(), post.getId(), after);

        // then
        Vote vote = voteRepository.findById(voteId).get();
        assertThat(vote.getPollChoiceId()).isEqualTo(after);
    }

    @Test
    @DisplayName("복수 투표하기")
    void multipleVote() {
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
        Long first = pollChoices.get(0).getId();
        Long second = pollChoices.get(1).getId();

        // when
        Long voteId1 = voteService.vote(user.getId(), post.getId(), first);
        Long voteId2 = voteService.vote(user.getId(), post.getId(), second);

        // then
        Vote vote1 = voteRepository.findById(voteId1).get();
        Vote vote2 = voteRepository.findById(voteId2).get();
        assertAll(
                () -> assertThat(vote1.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote1.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote1.getPollChoiceId()).isEqualTo(first),

                () -> assertThat(vote2.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote2.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote2.getPollChoiceId()).isEqualTo(second)
        );
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

        // when
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()))
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

        // when
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표하기 - 마감 시간 지난 경우")
    void vote_afterCloseDate() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .closeOption(
                                PostFixture.createCloseOptionBuilder()
                                        .closeType(CloseType.DATE)
                                        .closedAt(LocalDateTime.now())
                                        .build())
                        .build()
        );

        // when
        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 취소")
    void cancelVote() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());

        // when
        voteService.cancelVote(user.getId(), voteId);

        // then
        boolean res = voteRepository.findById(voteId).isEmpty();
        assertThat(res).isTrue();
    }

    @Test
    @DisplayName("투표 취소 - 투표자가 아닌 경우")
    void cancelVote_notVoter() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());

        // when then
        assertThatThrownBy(() -> voteService.cancelVote(2L, voteId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_VOTER.getMessage());
    }

//    @Test
//    @DisplayName("투표 현황 조회")
//    void findVoteStatus() throws Exception {
//        //given
//        User user = userRepository.save(createUser(1));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        ImageFile imageFile3 = imageFileRepository.save(createImageFile(3));
//        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, List.of(imageFile1, imageFile2, imageFile3), 1));
//        voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(1).getId());
//
//        //when
//        var response = voteService.findVoteStatus(user.getId(), post.getId());
//
//        //then
//        assertAll(
//                () -> assertThat(response).hasSize(3),
//                () -> assertThat(response.get(0).id()).isEqualTo(post.getPollChoices().get(1).getId()),
//                () -> assertThat(response.get(0).title()).isEqualTo(post.getPollChoices().get(1).getName()),
//                () -> assertThat(response.get(0).voteCount()).isEqualTo(1),
//                () -> assertThat(response.get(0).voteRatio()).isEqualTo("100.0"),
//
//                () -> assertThat(response.get(1).id()).isEqualTo(post.getPollChoices().get(0).getId()),
//                () -> assertThat(response.get(1).title()).isEqualTo(post.getPollChoices().get(0).getName()),
//                () -> assertThat(response.get(1).voteCount()).isEqualTo(0),
//                () -> assertThat(response.get(1).voteRatio()).isEqualTo("0.0"),
//
//                () -> assertThat(response.get(2).id()).isEqualTo(post.getPollChoices().get(2).getId()),
//                () -> assertThat(response.get(2).title()).isEqualTo(post.getPollChoices().get(2).getName()),
//                () -> assertThat(response.get(2).voteCount()).isEqualTo(0),
//                () -> assertThat(response.get(2).voteRatio()).isEqualTo("0.0")
//        );
//    }
//
//    @Test
//    @DisplayName("투표 현황 조회 - 투표한 사람인 경우")
//    void findVoteStatus_voteUser() throws Exception {
//        //given
//        User author = userRepository.save(createUser(1));
//        User voter = userRepository.save(createUser(2));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(createPost(author.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
//        voteService.vote(voter.getId(), post.getId(), post.getPollChoices().get(0).getId());
//
//        //when
//        var response = voteService.findVoteStatus(voter.getId(), post.getId());
//
//        //then
//        assertAll(
//                () -> assertThat(response).hasSize(2),
//                () -> assertThat(response.get(0).id()).isEqualTo(post.getPollChoices().get(0).getId()),
//                () -> assertThat(response.get(0).title()).isEqualTo(post.getPollChoices().get(0).getName()),
//                () -> assertThat(response.get(0).voteCount()).isEqualTo(1),
//                () -> assertThat(response.get(0).voteRatio()).isEqualTo("100.0"),
//                () -> assertThat(response.get(1).id()).isEqualTo(post.getPollChoices().get(1).getId()),
//                () -> assertThat(response.get(1).title()).isEqualTo(post.getPollChoices().get(1).getName()),
//                () -> assertThat(response.get(1).voteCount()).isEqualTo(0),
//                () -> assertThat(response.get(1).voteRatio()).isEqualTo("0.0")
//        );
//    }
//
//    @Test
//    @DisplayName("투표 현황 조회 - 작성자 아니고 투표 안 한 사람인 경우")
//    void findVoteStatus_notAuthorAndVoter() throws Exception {
//        //given
//        User author = userRepository.save(createUser(1));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(createPost(author.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
//
//        //when
//        assertThatThrownBy(() -> voteService.findVoteStatus(2L, post.getId()))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.ACCESS_DENIED_VOTE_STATUS.getMessage());
//    }

}
