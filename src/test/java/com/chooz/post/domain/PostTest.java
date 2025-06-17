package com.chooz.post.domain;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.chooz.support.fixture.PostFixture.createDefaultPost;
import static com.chooz.support.fixture.PostFixture.createPostBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    @DisplayName("게시글 생성")
    void create() throws Exception {
        //given
        long userId = 1L;
        String title = "title";
        String description = "description";
        List<PollChoice> pollChoices = List.of(
                PollChoice.create("title1", "http://example.com/image1"),
                PollChoice.create("title2", "http://example.com/image2")
        );

        //when
        Post post = Post.create(
                userId,
                title,
                description,
                pollChoices,
                "http://example.com/shareurl",
                PollOption.create(PollType.SINGLE, Scope.PUBLIC, CommentActive.OPEN),
                CloseOption.create(CloseType.SELF, null, null)
        );

        //then
        assertAll(
                () -> assertThat(post.getUserId()).isEqualTo(userId),
                () -> assertThat(post.getDescription()).isEqualTo(description),
                () -> assertThat(post.getStatus()).isEqualTo(Status.PROGRESS),
                () -> assertThat(post.getPollChoices()).hasSize(2),
                () -> assertThat(post.getShareUrl()).isEqualTo("http://example.com/shareurl"),
                () -> assertThat(post.getPollOption().getPollType()).isEqualTo(PollType.SINGLE),
                () -> assertThat(post.getPollOption().getScope()).isEqualTo(Scope.PUBLIC),
                () -> assertThat(post.getCloseOption().getCloseType()).isEqualTo(CloseType.SELF),
                () -> assertThat(post.getCloseOption().getClosedAt()).isNull(),
                () -> assertThat(post.getCloseOption().getMaxVoterCount()).isNull(),
                () -> assertThat(post.getPollChoices().get(0).getTitle()).isEqualTo("title1"),
                () -> assertThat(post.getPollChoices().get(0).getImageUrl()).isEqualTo("http://example.com/image1"),
                () -> assertThat(post.getPollChoices().get(1).getTitle()).isEqualTo("title2"),
                () -> assertThat(post.getPollChoices().get(1).getImageUrl()).isEqualTo("http://example.com/image2")
        );
    }

    @Test
    @DisplayName("게시글 생성 - 이미지가 2개 미만인 경우")
    void create_invalidPollChoiceCount() throws Exception {
        //given
        List<PollChoice> pollChoices = List.of(
                PollChoice.create("title1", "http://example.com/image1")
        );

        //when then
        assertThatThrownBy(() -> createPostBuilder().pollChoices(pollChoices).build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_POLL_CHOICE_COUNT.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 - 이미지가 10개 초과인 경우")
    void create_invalidPollChoiceCount2() throws Exception {
        //given
        List<PollChoice> pollChoices = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            pollChoices.add(PollChoice.create("title" + i, "http://example.com/image" + i));
        }

        //when then
        assertThatThrownBy(() -> createPostBuilder().pollChoices(pollChoices).build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.INVALID_POLL_CHOICE_COUNT.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 - 설명이 50자 넘어가는 경우")
    void create_titleCountExceeded() throws Exception {
        //given
        String title = "a".repeat(51);

        //when then
        assertThatThrownBy(() -> createPostBuilder().title(title).build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.TITLE_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("게시글 생성 - 설명이 100자 넘어가는 경우")
    void create_descriptionCountExceeded() throws Exception {
        //given
        String description = "a".repeat(101);

        //when then
        assertThatThrownBy(() -> createPostBuilder().description(description).build())
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("투표 마감")
    void closeByAuthor() throws Exception {
        //given
        long userId = 1L;
        Post post = createDefaultPost(userId);

        //when
        post.closeByAuthor(userId);

        //then
        assertThat(post.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    @DisplayName("투표 마감 - 이미 마감된 게시글인 경우")
    void close_ByAuthor_alreadyClosed() throws Exception {
        //given
        long userId = 1L;
        Post post = createPostBuilder()
                .userId(userId)
                .status(Status.CLOSED)
                .build();

        //when then
        assertThatThrownBy(() -> post.closeByAuthor(userId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.POST_ALREADY_CLOSED.getMessage());
    }

    @Test
    @DisplayName("투표 마감 - 게시글 작성자가 아닌 경우")
    void close_ByAuthor_notPostAuthor() throws Exception {
        //given
        long userId = 1L;
        Post post = createPostBuilder()
                .userId(userId)
                .build();

        //when then
        assertThatThrownBy(() -> post.closeByAuthor(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_AUTHOR.getMessage());
    }

    @Test
    @DisplayName("게시글 공개 범위 수정")
    void toggleScope() throws Exception {
        //given
        long userId = 1L;
        Post post = createPostBuilder()
                .userId(userId)
                .pollOption(PollOption.create(PollType.SINGLE, Scope.PRIVATE, CommentActive.OPEN))
                .build();

        //when then
        post.toggleScope(userId);
        assertThat(post.getPollOption().getScope()).isEqualTo(Scope.PUBLIC);

        //when then
        post.toggleScope(userId);
        assertThat(post.getPollOption().getScope()).isEqualTo(Scope.PRIVATE);
    }

    @Test
    @DisplayName("게시글 공개 범위 수정 - 게시글 작성자가 아닌 경우")
    void toggleScope_notPostAuthor() throws Exception {
        //given
        long userId = 1L;
        Post post = createPostBuilder()
                .userId(userId)
                .pollOption(PollOption.create(PollType.SINGLE, Scope.PRIVATE, CommentActive.OPEN))
                .build();

        //when then
        assertThatThrownBy(() -> post.toggleScope(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorCode.NOT_POST_AUTHOR.getMessage());
    }

    @Test
    @DisplayName("게시글 베스트 픽 조회")
    @Disabled
    void getBestPickedImage() throws Exception {
//        //given
//        long userId = 1L;
//        List<PollChoice> pollChoices = List.of(
//                PollChoice.create("뽀또A", 1L),
//                PollChoice.create("뽀또B", 2L)
//        );
//        Post post = new Post(null, userId, "description", Status.PROGRESS, Scope.PRIVATE, pollChoices, "shareUrl", VoteType.SINGLE);
//        post.getPollChoices().get(0).increaseVoteCount();
//        post.getPollChoices().get(0).increaseVoteCount();
//        post.getPollChoices().get(1).increaseVoteCount();
//
//        //when
//        PollChoice bestPickedImage = post.getBestPickedImage();
//
//        //then
//        assertThat(bestPickedImage.getName()).isEqualTo("뽀또A");
    }

    @Test
    @DisplayName("게시글 베스트 픽 조회 - 동일 투표수인 경우 첫 번째 이미지가 선택됨")
    @Disabled
    void getBestPickedImage_saveVoteCount() throws Exception {
//        //given
//        long userId = 1L;
//        List<PollChoice> pollChoices = List.of(
//                PollChoice.create("뽀또A", 1L),
//                PollChoice.create("뽀또B", 2L)
//        );
//        Post post = new Post(null, userId, "description", Status.PROGRESS, Scope.PRIVATE, pollChoices, "shareUrl", VoteType.SINGLE);
//        post.getPollChoices().get(0).increaseVoteCount();
//        post.getPollChoices().get(1).increaseVoteCount();
//
//        //when
//        PollChoice bestPickedImage = post.getBestPickedImage();
//
//        //then
//        assertThat(bestPickedImage.getName()).isEqualTo("뽀또A");
    }
}
