package com.chooz.commentLike.presentation;

import com.chooz.commentLike.presentation.dto.CommentLikeIdResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
        CommentLikeIdResponse commentLikeIdResponse = new CommentLikeIdResponse(commentLikeId);
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
                                        .description("댓글좋아요 ID")
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 좋아요 삭제")
    void deleteCommentLike() throws Exception {
        //when
        doNothing().when(commentLikeService).deleteCommentLike(commentLikeId, userId);
        //given
        mockMvc.perform(delete("/comment-likes/{commentLikeId}", commentLikeId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("commentLikeId").description("댓글 좋아요 ID")
                        )
                ));
    }
}
