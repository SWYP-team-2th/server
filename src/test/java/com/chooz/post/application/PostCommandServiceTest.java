package com.chooz.post.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.post.domain.*;
import com.chooz.post.presentation.dto.CloseOptionDto;
import com.chooz.post.presentation.dto.CreatePostRequest;
import com.chooz.post.presentation.dto.CreatePostResponse;
import com.chooz.post.presentation.dto.PollChoiceRequestDto;
import com.chooz.post.presentation.dto.PollOptionDto;
import com.chooz.post.presentation.dto.UpdatePostRequest;
import com.chooz.support.IntegrationTest;
import com.chooz.support.fixture.PostFixture;
import com.chooz.support.fixture.UserFixture;
import com.chooz.support.fixture.VoteFixture;
import com.chooz.thumbnail.domain.Thumbnail;
import com.chooz.thumbnail.domain.ThumbnailRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.domain.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class PostCommandServiceTest extends IntegrationTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    ShareUrlService shareUrlService;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    VoteRepository voteRepository;
    
    @Test
    @DisplayName("게시글 작성")
    void create() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "title",
                "description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null)
        );
        String shareUrl = "shareUrl";
        given(shareUrlService.generateShareUrl())
                .willReturn("shareUrl");

        //when
        CreatePostResponse response = postService.create(userId, request);

        //then
        Post post = postRepository.findById(response.postId()).get();
        Thumbnail thumbnail = thumbnailRepository.findByPostId(post.getId()).get();
        List<PollChoice> pollChoices = post.getPollChoices();
        assertAll(
                () -> assertThat(post.getDescription()).isEqualTo("description"),
                () -> assertThat(post.getUserId()).isEqualTo(userId),
                () -> assertThat(post.getShareUrl()).isEqualTo(shareUrl),
                () -> assertThat(post.getStatus()).isEqualTo(Status.PROGRESS),
                () -> assertThat(post.getPollOption().getPollType()).isEqualTo(PollType.SINGLE),

                () -> assertThat(pollChoices).hasSize(2),
                () -> assertThat(pollChoices.get(0).getImageUrl()).isEqualTo("http://image1.com"),
                () -> assertThat(pollChoices.get(0).getTitle()).isEqualTo("title1"),
                () -> assertThat(pollChoices.get(1).getImageUrl()).isEqualTo("http://image2.com"),
                () -> assertThat(pollChoices.get(1).getTitle()).isEqualTo("title2"),

                () -> assertThat(thumbnail.getThumbnailUrl()).isEqualTo("http://image1.com"),
                () -> assertThat(thumbnail.getPostId()).isEqualTo(post.getId()),
                () -> assertThat(thumbnail.getPollChoiceId()).isEqualTo(pollChoices.get(0).getId())
        );
    }

    @Test
    @DisplayName("게시글 작성 - 이미지가 2개 미만인 경우")
    void create_invalidPollChoiceCount() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "title",
                "description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com")
                ),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null)
        );
        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_POLL_CHOICE_COUNT.getMessage());
    }

    @Test
    @DisplayName("게시글 작성 - 설명이 100자 넘어가는 경우")
    void create_descriptionCountExceeded() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "title",
                "a".repeat(101),
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null)
        );

        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 작성 - 제목이 50자 넘어가는 경우")
    void create_titleCountExceeded() throws Exception {
        //given
        long userId = 1L;
        CreatePostRequest request = new CreatePostRequest(
                "a".repeat(51),
                "description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null)
        );

        //when then
        assertThatThrownBy(() -> postService.create(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.TITLE_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("투표 마감")
    void close() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));

        //when
        postService.close(user.getId(), post.getId());

        //then
        postRepository.findById(post.getId()).get();
        assertThat(post.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    @DisplayName("투표 마감 - 게시글 작성자가 아닐 경우")
    void close_notPostAuthor() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        User anotherUser = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));

        //when then
        assertThatThrownBy(() -> postService.close(anotherUser.getId(), post.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_AUTHOR.getMessage());
    }

    @Test
    @DisplayName("투표 마감 - 이미 마감된 게시글인 경우")
    void close_alreadyClosed() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .userId(user.getId())
                        .status(Status.CLOSED)
                        .build()
        );

        //when then
        assertThatThrownBy(() -> postService.close(user.getId(), post.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 마감 - 존재하지 않는 게시글일 경우")
    void close_notFoundPost() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> postService.close(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));

        //when
        postService.delete(user.getId(), post.getId());

        //then
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    @DisplayName("게시글 수정")
    void update() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PRIVATE, PollType.MULTIPLE, CommentActive.CLOSED),
                new CloseOptionDto(CloseType.SELF, null, null)
        );

        //when
        postService.update(user.getId(), post.getId(), request);

        //then
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertAll(
                () -> assertThat(updatedPost.getTitle()).isEqualTo(request.title()),
                () -> assertThat(updatedPost.getDescription()).isEqualTo(request.description()),
                () -> assertThat(updatedPost.getPollOption().getPollType()).isEqualTo(request.pollOption().pollType()),
                () -> assertThat(updatedPost.getPollOption().getScope()).isEqualTo(request.pollOption().scope()),
                () -> assertThat(updatedPost.getPollOption().getCommentActive()).isEqualTo(request.pollOption().commentActive()),
                () -> assertThat(updatedPost.getCloseOption().getCloseType()).isEqualTo(CloseType.SELF)
        );
    }

    @Test
    @DisplayName("게시글 수정 - 게시글 작성자가 아닐 경우")
    void update_notPostAuthor() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        User anotherUser = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PRIVATE, PollType.MULTIPLE, CommentActive.CLOSED),
                new CloseOptionDto(CloseType.SELF, null, null)
        );


        //when then
        assertThatThrownBy(() -> postService.update(anotherUser.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_AUTHOR.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 - 이미 마감된 게시글인 경우")
    void update_alreadyClosed() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .userId(user.getId())
                        .status(Status.CLOSED)
                        .build()
        );
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PRIVATE, PollType.MULTIPLE, CommentActive.CLOSED),
                new CloseOptionDto(CloseType.SELF, null, null)
        );


        //when then
        assertThatThrownBy(() -> postService.update(user.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 - 제목이 50자를 초과하는 경우")
    void update_titleLengthExceeded() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PRIVATE, PollType.MULTIPLE, CommentActive.CLOSED),
                new CloseOptionDto(CloseType.SELF, null, null)
        );


        //when then
        assertThatThrownBy(() -> postService.update(user.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.TITLE_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 - 설명이 100자를 초과하는 경우")
    void update_descriptionLengthExceeded() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(PostFixture.createDefaultPost(user.getId()));
        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(
                        new PollChoiceRequestDto("title1", "http://image1.com"),
                        new PollChoiceRequestDto("title2", "http://image2.com")
                ),
                new PollOptionDto(Scope.PRIVATE, PollType.MULTIPLE, CommentActive.CLOSED),
                new CloseOptionDto(CloseType.SELF, null, null)
        );


        //when then
        assertThatThrownBy(() -> postService.update(user.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 - DATE 타입 마감 옵션에서 과거 날짜로 설정하는 경우")
    void update_invalidPastDateCloseOption() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .userId(user.getId())
                        .closeOption(
                                CloseOption.create(CloseType.DATE, LocalDateTime.now().plusDays(1), null)
                        )
                        .build()
        );

        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.DATE, LocalDateTime.now().minusDays(1), null)
        );

        //when then
        assertThatThrownBy(() -> postService.update(user.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_DATE_CLOSE_OPTION.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 - DATE 타입 마감 옵션에서 생성 시간 기준 1시간 이내로 설정하는 경우")
    void update_invalidDateCloseOptionWithinOneHour() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .userId(user.getId())
                        .closeOption(
                                CloseOption.create(CloseType.DATE, LocalDateTime.now().plusDays(1), null)
                        )
                        .build()
        );

        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.DATE, LocalDateTime.now().plusMinutes(30), null)
        );

        //when then
        assertThatThrownBy(() -> postService.update(user.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_DATE_CLOSE_OPTION.getMessage());
    }

    @Test
    @DisplayName("게시글 수정 - VOTER 타입 마감 옵션에서 현재 투표자 수보다 적은 값으로 설정하는 경우")
    void update_invalidVoterCloseOption() throws Exception {
        //given
        User user = userRepository.save(UserFixture.createDefaultUser());
        User voter1 = userRepository.save(UserFixture.createDefaultUser());
        User voter2 = userRepository.save(UserFixture.createDefaultUser());

        Post post = postRepository.save(
                PostFixture.createPostBuilder()
                        .userId(user.getId())
                        .closeOption(
                                CloseOption.create(CloseType.VOTER, null, 10)
                        )
                        .build()
        );

        // 투표 데이터 생성 (2명의 투표자)
        voteRepository.save(VoteFixture.createDefaultVote(voter1.getId(), post.getId(), post.getPollChoices().get(0).getId()));
        voteRepository.save(VoteFixture.createDefaultVote(voter2.getId(), post.getId(), post.getPollChoices().get(1).getId()));

        UpdatePostRequest request = new UpdatePostRequest(
                "Updated Title",
                "Updated Description",
                List.of(),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.VOTER, null, 1) // 1명으로 설정 (현재 2명 투표함)
        );

        //when then
        assertThatThrownBy(() -> postService.update(user.getId(), post.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_VOTER_CLOSE_OPTION.getMessage());
    }

}
