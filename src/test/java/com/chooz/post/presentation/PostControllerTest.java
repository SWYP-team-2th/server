package com.chooz.post.presentation;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Scope;
import com.chooz.post.domain.Status;
import com.chooz.post.presentation.dto.AuthorDto;
import com.chooz.post.presentation.dto.CloseOptionDto;
import com.chooz.post.presentation.dto.CreatePostRequest;
import com.chooz.post.presentation.dto.CreatePostResponse;
import com.chooz.post.presentation.dto.FeedResponse;
import com.chooz.post.presentation.dto.MostVotedPollChoiceDto;
import com.chooz.post.presentation.dto.MyPagePostResponse;
import com.chooz.post.presentation.dto.PollChoiceRequestDto;
import com.chooz.post.presentation.dto.PollChoiceResponse;
import com.chooz.post.presentation.dto.PollChoiceVoteResponse;
import com.chooz.post.presentation.dto.PollOptionDto;
import com.chooz.post.presentation.dto.PostResponse;
import com.chooz.post.presentation.dto.UpdatePostRequest;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 생성")
    void createPost() throws Exception {
        //given
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
        CreatePostResponse response = new CreatePostResponse(1L, "shareUrl");
        given(postService.create(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(constraints("1~50자 사이")),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("설명")
                                        .attributes(constraints("0~100자 사이")),
                                fieldWithPath("pollChoices")
                                        .type(JsonFieldType.ARRAY)
                                        .description("투표 선택지")
                                        .attributes(constraints("최소 2개 최대 10개")),
                                fieldWithPath("pollChoices[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("투표 선택지 제목"),
                                fieldWithPath("pollChoices[].imageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("투표 선택지 이미지 url"),
                                fieldWithPath("pollOption")
                                        .type(JsonFieldType.OBJECT)
                                        .description("투표 옵션"),
                                fieldWithPath("pollOption.scope")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("투표 공개 범위", Scope.class)),
                                fieldWithPath("pollOption.pollType")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("투표 방식", PollType.class)),
                                fieldWithPath("pollOption.commentActive")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("게시글 댓글 활성화 여부", CommentActive.class)),
                                fieldWithPath("closeOption")
                                        .type(JsonFieldType.OBJECT)
                                        .description("투표 마감 옵션"),
                                fieldWithPath("closeOption.closeType")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("투표 마감 방식", CloseType.class)),
                                fieldWithPath("closeOption.closedAt")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("투표 마감 시간  (now + 1h < closedAt)"),
                                fieldWithPath("closeOption.maxVoterCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("투표 최대 참여자 수 (1 < maxVoterCount < 999)")
                        ),
                        responseFields(
                                fieldWithPath("postId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("shareUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 공유 url")
                        )
                ));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 공유 url 상세 조회")
    void findPost_shareUrl() throws Exception {
        PostResponse response = new PostResponse(
                1L,
                "title",
                "description",
                new AuthorDto(
                        1L,
                        "author",
                        "https://image.chooz.site/profile-image"
                ),
                List.of(
                        new PollChoiceVoteResponse(1L, "title1", "https://image.chooz.site/image/1", 1L),
                        new PollChoiceVoteResponse(2L, "title2", "https://image.chooz.site/image/2", null)
                ),
                "https://chooz.site/shareurl",
                true,
                Status.PROGRESS,
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null),
                0L,
                1L,
                LocalDateTime.of(2025, 2, 13, 12, 0)
        );
        //given
        given(postService.findByShareUrl(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/shareUrl/{shareUrl}", "JNOfBVfcG2z89afSiRrOyQ"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("shareUrl").description("공유 url")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("게시글 설명"),
                                fieldWithPath("author").type(JsonFieldType.OBJECT).description("게시글 작성자 정보"),
                                fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("게시글 작성자 유저 Id"),
                                fieldWithPath("author.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("author.profileUrl").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("pollChoices[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("pollChoices[].id").type(JsonFieldType.NUMBER).description("투표 선택지 Id"),
                                fieldWithPath("pollChoices[].title").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("pollChoices[].imageUrl").type(JsonFieldType.STRING).description("사진 이미지"),
                                fieldWithPath("pollChoices[].voteId").type(JsonFieldType.NUMBER).optional().description("투표 Id (투표 안 한 경우 null)"),
                                fieldWithPath("shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("pollOption").type(JsonFieldType.OBJECT).description("투표 설정"),
                                fieldWithPath("pollOption.pollType").type(JsonFieldType.STRING).description(enumDescription("단일/복수 투표", PollType.class)),
                                fieldWithPath("pollOption.scope").type(JsonFieldType.STRING).description(enumDescription("공개 여부", Scope.class)),
                                fieldWithPath("pollOption.commentActive").type(JsonFieldType.STRING).description(enumDescription("댓글 활성화 여부", CommentActive.class)),
                                fieldWithPath("closeOption").type(JsonFieldType.OBJECT).description("마감 설정"),
                                fieldWithPath("closeOption.closeType").type(JsonFieldType.STRING).description(enumDescription("마감 방식", CloseType.class)),
                                fieldWithPath("closeOption.closedAt").type(JsonFieldType.STRING).optional().description("마감 시간, (closeType이 DATE일 경우 NN)"),
                                fieldWithPath("closeOption.maxVoterCount").type(JsonFieldType.NUMBER).optional().description("남은 투표 참여자 수 (closeType이 VOTER_COUNT일 경우 NN)"),
                                fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                fieldWithPath("voterCount").type(JsonFieldType.NUMBER).description("투표 참여자 수"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("게시글 마감 여부 (PROGRESS, CLOSED)"),
                                fieldWithPath("isAuthor").type(JsonFieldType.BOOLEAN).description("게시글 작성자 여부"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 작성 시간")
                        )
                ));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("게시글 상세 조회")
    void findPost() throws Exception {
        PostResponse response = new PostResponse(
                1L,
                "title",
                "description",
                new AuthorDto(
                        1L,
                        "author",
                        "https://image.chooz.site/profile-image"
                ),
                List.of(
                        new PollChoiceVoteResponse(1L, "title1", "https://image.chooz.site/image/1", 1L),
                        new PollChoiceVoteResponse(2L, "title2", "https://image.chooz.site/image/2", null)
                ),
                "https://chooz.site/shareurl",
                true,
                Status.PROGRESS,
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null),
                0L,
                1L,
                LocalDateTime.of(2025, 2, 13, 12, 0)
        );
        //given
        given(postService.findById(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("게시글 설명"),
                                fieldWithPath("author").type(JsonFieldType.OBJECT).description("게시글 작성자 정보"),
                                fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("게시글 작성자 유저 Id"),
                                fieldWithPath("author.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("author.profileUrl").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("pollChoices[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("pollChoices[].id").type(JsonFieldType.NUMBER).description("투표 선택지 Id"),
                                fieldWithPath("pollChoices[].title").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("pollChoices[].imageUrl").type(JsonFieldType.STRING).description("사진 이미지"),
                                fieldWithPath("pollChoices[].voteId").type(JsonFieldType.NUMBER).optional().description("투표 Id (투표 안 한 경우 null)"),
                                fieldWithPath("shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("pollOption").type(JsonFieldType.OBJECT).description("투표 설정"),
                                fieldWithPath("pollOption.pollType").type(JsonFieldType.STRING).description(enumDescription("단일/복수 투표", PollType.class)),
                                fieldWithPath("pollOption.scope").type(JsonFieldType.STRING).description(enumDescription("공개 여부", Scope.class)),
                                fieldWithPath("pollOption.commentActive").type(JsonFieldType.STRING).description(enumDescription("댓글 활성화 여부", CommentActive.class)),
                                fieldWithPath("closeOption").type(JsonFieldType.OBJECT).description("마감 설정"),
                                fieldWithPath("closeOption.closeType").type(JsonFieldType.STRING).description(enumDescription("마감 방식", CloseType.class)),
                                fieldWithPath("closeOption.closedAt").type(JsonFieldType.STRING).optional().description("마감 시간, (closeType이 DATE일 경우 NN)"),
                                fieldWithPath("closeOption.maxVoterCount").type(JsonFieldType.NUMBER).optional().description("남은 투표 참여자 수 (closeType이 VOTER_COUNT일 경우 NN)"),
                                fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("댓글 수"),
                                fieldWithPath("voterCount").type(JsonFieldType.NUMBER).description("투표 참여자 수"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("게시글 마감 여부 (PROGRESS, CLOSED)"),
                                fieldWithPath("isAuthor").type(JsonFieldType.BOOLEAN).description("게시글 작성자 여부"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 작성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 삭제")
    void deletePost() throws Exception {
        //given

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/posts/{postId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        )
                ));
        verify(postService, times(1)).delete(any(), any());
    }

    @Test
    @WithMockUserInfo
    @DisplayName("유저가 작성한 게시글 조회")
    void findMyPost() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new MyPagePostResponse(
                                1L,
                                "title",
                                "https://image.chooz.site/1",
                                Status.PROGRESS,
                                new CloseOptionDto(CloseType.SELF, null, null),
                                new MyPagePostResponse.PostVoteInfo(5L, new MostVotedPollChoiceDto(1L, "title", 5, "50%")),
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );
        given(postService.findUserPosts(1L, 1L, null, 10))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/users/{userId}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("userId").description("유저 Id")),
                        requestHeaders(authorizationHeader()),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("nextCursor")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("다음 조회 커서 값"),
                                fieldWithPath("hasNext")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("다음 페이지 존재 여부 (기본 값 10)"),
                                fieldWithPath("data[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("게시글 데이터"),
                                fieldWithPath("data[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("data[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목"),
                                fieldWithPath("data[].thumbnailImageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("썸네일 이미지 URL"),
                                fieldWithPath("data[].status")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("게시글 상태", Status.class)),
                                fieldWithPath("data[].closeOptionDto")
                                        .type(JsonFieldType.OBJECT)
                                        .description("게시글 마감 옵션"),
                                fieldWithPath("data[].closeOptionDto.closeType")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("마감 방식", CloseType.class)),
                                fieldWithPath("data[].closeOptionDto.closedAt")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("마감 시간 (closeType이 DATE일 경우)"),
                                fieldWithPath("data[].closeOptionDto.maxVoterCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("최대 투표자 수 (closeType이 VOTER일 경우)"),
                                fieldWithPath("data[].postVoteInfo")
                                        .type(JsonFieldType.OBJECT)
                                        .description("게시글 투표 정보"),
                                fieldWithPath("data[].postVoteInfo.totalVoterCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("총 투표자 수"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice")
                                        .type(JsonFieldType.OBJECT)
                                        .optional()
                                        .description("가장 많은 투표를 받은 선택지 정보"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.id")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("선택지 ID"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.title")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("선택지 제목"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.voteCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("선택지 투표 수"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.voteRatio")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("선택지 투표 비율"),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 생성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("유저가 참여한 게시글 조회")
    void findVotedPost() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new MyPagePostResponse(
                                1L,
                                "title",
                                "https://image.chooz.site/1",
                                Status.PROGRESS,
                                new CloseOptionDto(CloseType.SELF, null, null),
                                new MyPagePostResponse.PostVoteInfo(5L, new MostVotedPollChoiceDto(1L, "title", 5, "50%")),
                                LocalDateTime.of(2025, 2, 13, 12, 0)
                        )
                )
        );
        given(postService.findVotedPosts(1L, 1L, null, 10))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/users/{userId}/voted", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("userId").description("유저 Id")),
                        requestHeaders(authorizationHeader()),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("nextCursor")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("다음 조회 커서 값"),
                                fieldWithPath("hasNext")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("다음 페이지 존재 여부 (기본 값 10)"),
                                fieldWithPath("data[]")
                                        .type(JsonFieldType.ARRAY)
                                        .description("게시글 데이터"),
                                fieldWithPath("data[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("data[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목"),
                                fieldWithPath("data[].thumbnailImageUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("썸네일 이미지 URL"),
                                fieldWithPath("data[].status")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("게시글 상태", Status.class)),
                                fieldWithPath("data[].closeOptionDto")
                                        .type(JsonFieldType.OBJECT)
                                        .description("게시글 마감 옵션"),
                                fieldWithPath("data[].closeOptionDto.closeType")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("마감 방식", CloseType.class)),
                                fieldWithPath("data[].closeOptionDto.closedAt")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("마감 시간 (closeType이 DATE일 경우)"),
                                fieldWithPath("data[].closeOptionDto.maxVoterCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("최대 투표자 수 (closeType이 VOTER일 경우)"),
                                fieldWithPath("data[].postVoteInfo")
                                        .type(JsonFieldType.OBJECT)
                                        .description("게시글 투표 정보"),
                                fieldWithPath("data[].postVoteInfo.totalVoterCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("총 투표자 수"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice")
                                        .type(JsonFieldType.OBJECT)
                                        .optional()
                                        .description("가장 많은 투표를 받은 선택지 정보"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.id")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("선택지 ID"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.title")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("선택지 제목"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.voteCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("선택지 투표 수"),
                                fieldWithPath("data[].postVoteInfo.mostVotedPollChoice.voteRatio")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("선택지 투표 비율"),
                                fieldWithPath("data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 생성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 수정")
    void updatePost() throws Exception {
        //given
        UpdatePostRequest request = new UpdatePostRequest(
                "title",
                "description",
                List.of(),
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null)
        );

        //when then
        mockMvc.perform(put("/posts/{postId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        requestFields(
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("게시글 제목")
                                        .attributes(constraints("1~50자 사이")),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("설명")
                                        .attributes(constraints("0~100자 사이")),
                                fieldWithPath("pollChoices")
                                        .type(JsonFieldType.ARRAY)
                                        .description("투표 선택지")
                                        .attributes(constraints("최소 2개 최대 10개")),
                                fieldWithPath("pollOption")
                                        .type(JsonFieldType.OBJECT)
                                        .description("투표 옵션"),
                                fieldWithPath("pollOption.scope")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("투표 공개 범위", Scope.class)),
                                fieldWithPath("pollOption.pollType")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("투표 방식", PollType.class)),
                                fieldWithPath("pollOption.commentActive")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("게시글 댓글 활성화 여부", CommentActive.class)),
                                fieldWithPath("closeOption")
                                        .type(JsonFieldType.OBJECT)
                                        .description("투표 마감 옵션"),
                                fieldWithPath("closeOption.closeType")
                                        .type(JsonFieldType.STRING)
                                        .description(enumDescription("투표 마감 방식", CloseType.class)),
                                fieldWithPath("closeOption.closedAt")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("투표 마감 시간 (now or createdAt + 1h < closedAt)"),
                                fieldWithPath("closeOption.maxVoterCount")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("투표 최대 참여자 수 (1 or 현재 투표 참여자 수 < maxVoterCount < 999)")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 수정 조회")
    void findPost_update() throws Exception {
        UpdatePostResponse response = new UpdatePostResponse(
                1L,
                "title",
                "description",
                List.of(
                        new PollChoiceResponse(1L, "title1", "https://image.chooz.site/image/1"),
                        new PollChoiceResponse(2L, "title2", "https://image.chooz.site/image/2")
                ),
                "https://chooz.site/shareurl",
                Status.PROGRESS,
                new PollOptionDto(Scope.PUBLIC, PollType.SINGLE, CommentActive.OPEN),
                new CloseOptionDto(CloseType.SELF, null, null),
                LocalDateTime.of(2025, 2, 13, 12, 0)
        );
        //given
        given(postService.findUpdatePost(any(), any()))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}/update", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("게시글 설명"),
                                fieldWithPath("pollChoices[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("pollChoices[].id").type(JsonFieldType.NUMBER).description("투표 선택지 Id"),
                                fieldWithPath("pollChoices[].title").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("pollChoices[].imageUrl").type(JsonFieldType.STRING).description("사진 이미지"),
                                fieldWithPath("shareUrl").type(JsonFieldType.STRING).description("게시글 공유 URL"),
                                fieldWithPath("pollOption").type(JsonFieldType.OBJECT).description("투표 설정"),
                                fieldWithPath("pollOption.pollType").type(JsonFieldType.STRING).description(enumDescription("단일/복수 투표", PollType.class)),
                                fieldWithPath("pollOption.scope").type(JsonFieldType.STRING).description(enumDescription("공개 여부", Scope.class)),
                                fieldWithPath("pollOption.commentActive").type(JsonFieldType.STRING).description(enumDescription("댓글 활성화 여부", CommentActive.class)),
                                fieldWithPath("closeOption").type(JsonFieldType.OBJECT).description("마감 설정"),
                                fieldWithPath("closeOption.closeType").type(JsonFieldType.STRING).description(enumDescription("마감 방식", CloseType.class)),
                                fieldWithPath("closeOption.closedAt").type(JsonFieldType.STRING).optional().description("마감 시간, (closeType이 DATE일 경우 NN)"),
                                fieldWithPath("closeOption.maxVoterCount").type(JsonFieldType.NUMBER).optional().description("남은 투표 참여자 수 (closeType이 VOTER_COUNT일 경우 NN)"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("게시글 마감 여부 (PROGRESS, CLOSED)"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("게시글 작성 시간")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 마감")
    void closeByAuthorPost() throws Exception {
        //given

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/posts/{postId}/close", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        )
                ));
        verify(postService, times(1)).close(any(), any());
    }

    @Test
    @WithMockUserInfo
    @DisplayName("피드 조회")
    void findFeed() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new FeedResponse(
                                1L,
                                new AuthorDto(
                                        1L,
                                        "author",
                                        "https://image.chooz.site/profile-image"
                                ),
                                Status.PROGRESS,
                                "title",
                                "http://example.com/image/1",
                                true,
                                1L,
                                2L,
                                LocalDateTime.now()
                        )
                )
        );
        given(postService.findFeed(1L, null, 10)).willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/feed")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).optional().description("다음 조회 커서 값"),
                                fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부 (기본 값 10)"),
                                fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("data[].author").type(JsonFieldType.OBJECT).description("게시글 작성자 정보"),
                                fieldWithPath("data[].author.id").type(JsonFieldType.NUMBER).description("게시글 작성자 유저 ID"),
                                fieldWithPath("data[].author.nickname").type(JsonFieldType.STRING).description("게시글 작성자 닉네임"),
                                fieldWithPath("data[].author.profileUrl").type(JsonFieldType.STRING).description("게시글 작성자 프로필 이미지"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("게시글 마감 여부 (PROGRESS, CLOSED)"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("data[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 이미지 url"),
                                fieldWithPath("data[].isAuthor").type(JsonFieldType.BOOLEAN).description("게시글 작성자 여부"),
                                fieldWithPath("data[].voterCount").type(JsonFieldType.NUMBER).description("투표 참여자 수"),
                                fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("투표 댓글 수"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("게시글 작성 날짜")
                        )
                ));
    }
}
