package com.chooz.comment.presentation;

import com.chooz.comment.presentation.dto.*;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends RestDocsTest {

    private final Long postId = 1L;
    private final Long userId = 1L;
    private final Long commentId = 10L;

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 목록 조회")
    void findComments() throws Exception {
        // given
        int size = 10;

        List<CommentDto> commentDtos = List.of(
                new CommentDto(
                        1L,
                        "comment1",
                        false,
                        LocalDateTime.of(2025, 2, 13, 12, 0),
                        new CommentAuthorDto(1L, "author", "https://image.chooz.site/profile-image"),
                        new CommentLikeDto(null, false, 10)
                ),
                new CommentDto(
                        2L,
                        "comment2",
                        true,
                        LocalDateTime.of(2025, 2, 13, 12, 1),
                        new CommentAuthorDto(2L, "author2", "https://image.chooz.site/profile-image2"),
                        new CommentLikeDto(2L, true, 5)
                )
        );

        CommentResponse commentResponse = new CommentResponse(
                2,
                CursorBasePaginatedResponse.of( new SliceImpl<>(
                        commentDtos,
                        PageRequest.of(0, size),
                false
        )));

        given(commentService.findComments(postId, userId, null, size))
                .willReturn(commentResponse);

        // when then
        mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .param("cursor", "")
                        .param("size", String.valueOf(size))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponse)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(parameterWithName("postId").description("게시글 ID")),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("commentCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글에 속한 댓글 수"),
                                fieldWithPath("comments.data[].id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("comments.data[].content")
                                        .type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("comments.data[].edited")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("수정 여부"),
                                fieldWithPath("comments.data[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("댓글 생성시간"),
                                fieldWithPath("comments.data[].author.userId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("작성자 ID"),
                                fieldWithPath("comments.data[].author.nickname")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("comments.data[].author.profileUrl")
                                        .type(JsonFieldType.STRING)
                                        .description("작성자 프로필 이미지 URL"),
                                fieldWithPath("comments.data[].like.commentLikeId")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("댓글 좋아요 ID(좋아요를 누르지 않은 경우, null)"),
                                fieldWithPath("comments.data[].like.liked")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("내가 댓글 좋아요 눌렀는지 여부"),
                                fieldWithPath("comments.data[].like.likeCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글 좋아요 수"),
                                fieldWithPath("comments.nextCursor")
                                        .type(JsonFieldType.NUMBER)
                                        .optional()
                                        .description("다음 커서"),
                                fieldWithPath("comments.hasNext")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("다음 페이지 존재 여부")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 생성")
    void createComments() throws Exception {
        CommentRequest request = new CommentRequest("테스트 댓글");

        CommentIdResponse response = new CommentIdResponse(commentId);

        given(commentService.createComment(postId, request, 1L))
                .willReturn(response);

        mockMvc.perform(post("/posts/{postId}/comments", postId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(parameterWithName("postId").description("게시글 ID")),
                        requestFields(fieldWithPath("content").description("댓글 내용")),
                        responseFields(
                                fieldWithPath("commentId").description("댓글 ID")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 수정")
    void updateComment() throws Exception {
        CommentRequest request = new CommentRequest("수정된 댓글 내용");

        CommentIdResponse response = new CommentIdResponse(commentId);

        given(commentService.updateComment(postId, commentId, request, 1L))
                .willReturn(response);

        mockMvc.perform(patch("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        requestFields(fieldWithPath("content").description("수정할 댓글 내용")),
                        responseFields(
                                fieldWithPath("commentId").description("댓글 ID")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", postId, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )
                ));
    }
}
