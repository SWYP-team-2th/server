package com.chooz.commentLike.presentation;

import com.chooz.commentLike.presentation.dto.CommentLikeIdResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentLikeControllerTest extends RestDocsTest {
    private final Long userId = 1L;
    private final Long commentId = 1L;
    private final Long commentLikeId = 1L;

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 좋아요 생성")
    void createCommentLike() throws Exception {
        //given
        CommentLikeIdResponse commentLikeIdResponse =
                new CommentLikeIdResponse(commentLikeId, 11);
        given(commentLikeService.createCommentLike(commentId, userId))
                .willReturn(commentLikeIdResponse);

        //when then
        mockMvc.perform(post("/comment-likes/{commentId}", commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(parameterWithName("commentId").description("댓글 ID")),
                        responseFields(
                                fieldWithPath("commentLikeId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글좋아요 ID"),
                                fieldWithPath("likeCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글좋아요 수")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 좋아요 삭제")
    void deleteCommentLike() throws Exception {
        //given
        CommentLikeIdResponse commentLikeIdResponse =
                new CommentLikeIdResponse(null, 10);
        given(commentLikeService.deleteCommentLike(commentId, commentLikeId, userId))
                .willReturn(commentLikeIdResponse);
        //when then
        mockMvc.perform(delete("/comment-likes/{commentId}/{commentLikeId}", commentId, commentLikeId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 ID"),
                                parameterWithName("commentLikeId").description("댓글 좋아요 ID")),
                        responseFields(
                                fieldWithPath("commentLikeId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글좋아요 ID")
                                        .optional(),
                                fieldWithPath("likeCount")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글좋아요 수")
                        )
                ));
    }
}
