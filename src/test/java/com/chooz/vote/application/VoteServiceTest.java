package com.chooz.vote.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.image.domain.ImageFile;
import com.chooz.image.domain.ImageFileRepository;
import com.chooz.post.domain.*;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.Vote;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest extends IntegrationTest {

    @Autowired
    VoteService voteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ImageFileRepository imageFileRepository;

    @Test
    @DisplayName("단일 투표하기")
    void singleVote() {
        // given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .pollOption(new PollOption(PollType.SINGLE, Scope.PUBLIC, CommentActive.OPEN))
                        .build()
        );

        // when
        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());

        // then
        Vote vote = voteRepository.findById(voteId).get();
        assertAll(
                () -> assertThat(vote.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(vote.getPollChoiceId()).isEqualTo(post.getPollChoices().get(0).getId())
        );
    }
//
//    @Test
//    @DisplayName("단일 투표하기 - 다른 이미지로 투표 변경한 경우")
//    void singleVote_change() {
//        // given
//        User user = userRepository.save(createUser(2));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
//        voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());
//
//        // when
//        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(1).getId());
//
//        // then
//        Vote vote = voteRepository.findById(voteId).get();
//        Post findPost = postRepository.findById(post.getId()).get();
//        assertAll(
//                () -> assertThat(vote.getUserId()).isEqualTo(user.getId()),
//                () -> assertThat(vote.getPostId()).isEqualTo(post.getId()),
//                () -> assertThat(vote.getPollChoiceId()).isEqualTo(post.getPollChoices().get(1).getId()),
//                () -> assertThat(findPost.getPollChoices().get(0).getVoteCount()).isEqualTo(0),
//                () -> assertThat(findPost.getPollChoices().get(1).getVoteCount()).isEqualTo(1)
//        );
//    }
//
//    @Test
//    @DisplayName("복수 투표하기")
//    void multipleVote() {
//        // given
//        User user = userRepository.save(createUser(1));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(createMultiplePost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
//
//        // when
//        Long voteId1 = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());
//        Long voteId2 = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(1).getId());
//
//        // then
//        Vote vote1 = voteRepository.findById(voteId1).get();
//        Vote vote2 = voteRepository.findById(voteId2).get();
//        Post findPost = postRepository.findById(post.getId()).get();
//        assertAll(
//                () -> assertThat(vote1.getUserId()).isEqualTo(user.getId()),
//                () -> assertThat(vote1.getPostId()).isEqualTo(post.getId()),
//                () -> assertThat(vote1.getPollChoiceId()).isEqualTo(post.getPollChoices().get(0).getId()),
//
//                () -> assertThat(vote2.getUserId()).isEqualTo(user.getId()),
//                () -> assertThat(vote2.getPostId()).isEqualTo(post.getId()),
//                () -> assertThat(vote2.getPollChoiceId()).isEqualTo(post.getPollChoices().get(1).getId()),
//
//                () -> assertThat(findPost.getPollChoices().get(0).getVoteCount()).isEqualTo(1),
//                () -> assertThat(findPost.getPollChoices().get(1).getVoteCount()).isEqualTo(1)
//        );
//    }
//
//    @Test
//    @DisplayName("투표하기 - 투표 마감된 경우")
//    void vote_alreadyClosed() {
//        // given
//        User user = userRepository.save(createUser(1));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(new Post(
//                null,
//                user.getId(),
//                "description",
//                Status.CLOSED,
//                Scope.PRIVATE,
//                List.of(
//                        PollChoice.create("뽀또A", imageFile1.getId()),
//                        PollChoice.create("뽀또B", imageFile2.getId())
//                ),
//                "shareUrl",
//                VoteType.SINGLE
//        ));
//
//        // when
//        assertThatThrownBy(() -> voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId()))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
//    }
//
//    @Test
//    @DisplayName("투표 취소")
//    void cancelVote() {
//        // given
//        User user = userRepository.save(createUser(1));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
//        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());
//
//        // when
//        voteService.cancelVote(user.getId(), voteId);
//
//        // then
//        boolean res = voteRepository.findById(voteId).isEmpty();
//        Post findPost = postRepository.findById(post.getId()).get();
//        assertAll(
//                () -> assertThat(res).isEqualTo(true),
//                () -> assertThat(findPost.getPollChoices().get(0).getVoteCount()).isEqualTo(0)
//        );
//    }
//
//    @Test
//    @DisplayName("투표 취소 - 투표자가 아닌 경우")
//    void cancelVote_notVoter() {
//        // given
//        User user = userRepository.save(createUser(1));
//        ImageFile imageFile1 = imageFileRepository.save(createImageFile(1));
//        ImageFile imageFile2 = imageFileRepository.save(createImageFile(2));
//        Post post = postRepository.save(createPost(user.getId(), Scope.PRIVATE, imageFile1, imageFile2, 1));
//        Long voteId = voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(0).getId());
//
//        // when then
//        assertThatThrownBy(() -> voteService.cancelVote(2L, voteId))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(ErrorCode.NOT_VOTER.getMessage());
//    }
//
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
//                () -> assertThat(response.get(0).imageName()).isEqualTo(post.getPollChoices().get(1).getName()),
//                () -> assertThat(response.get(0).voteCount()).isEqualTo(1),
//                () -> assertThat(response.get(0).voteRatio()).isEqualTo("100.0"),
//
//                () -> assertThat(response.get(1).id()).isEqualTo(post.getPollChoices().get(0).getId()),
//                () -> assertThat(response.get(1).imageName()).isEqualTo(post.getPollChoices().get(0).getName()),
//                () -> assertThat(response.get(1).voteCount()).isEqualTo(0),
//                () -> assertThat(response.get(1).voteRatio()).isEqualTo("0.0"),
//
//                () -> assertThat(response.get(2).id()).isEqualTo(post.getPollChoices().get(2).getId()),
//                () -> assertThat(response.get(2).imageName()).isEqualTo(post.getPollChoices().get(2).getName()),
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
//                () -> assertThat(response.get(0).imageName()).isEqualTo(post.getPollChoices().get(0).getName()),
//                () -> assertThat(response.get(0).voteCount()).isEqualTo(1),
//                () -> assertThat(response.get(0).voteRatio()).isEqualTo("100.0"),
//                () -> assertThat(response.get(1).id()).isEqualTo(post.getPollChoices().get(1).getId()),
//                () -> assertThat(response.get(1).imageName()).isEqualTo(post.getPollChoices().get(1).getName()),
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
