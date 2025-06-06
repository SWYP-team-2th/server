package com.chooz.comment.presentation;

import com.chooz.comment.presentation.dto.CommentAnchorResponse;
import com.chooz.comment.presentation.dto.CommentRequest;
import com.chooz.comment.presentation.dto.CommentResponse;
import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends RestDocsTest {

    private final Long postId = 1L;
    private final Long commentId = 10L;

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 목록 조회")
    void getComments() throws Exception {
        // given
        Long postId = 1L;
        Long cursor = null;
        int size = 10;

        CommentResponse response = new CommentResponse(
                1L,
                1L,
                "nicname",
                "www.example.com/profile.png",
                "댓글내용",
                0,
                10,
                false
        );

        CursorBasePaginatedResponse<CommentResponse> commentListResponse = CursorBasePaginatedResponse.of( new SliceImpl<>(
                singletonList(response),
                PageRequest.of(0, size),
                false
        ));

        when(commentService.getComments(eq(postId), any(), eq(cursor), eq(size)))
                .thenReturn(commentListResponse);

        // when then
        mockMvc.perform(get("/posts/{postId}/comments", postId)
                        .param("cursor", "")
                        .param("size", String.valueOf(size))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentListResponse)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(parameterWithName("postId").description("게시글 ID")),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("data[].id").description("댓글 ID"),
                                fieldWithPath("data[].userId").description("작성자 ID"),
                                fieldWithPath("data[].nickname").description("작성자 닉네임"),
                                fieldWithPath("data[].profileUrl").description("작성자 프로필 이미지 URL"),
                                fieldWithPath("data[].content").description("댓글 내용"),
                                fieldWithPath("data[].edited").description("수정 여부 (0이면 원본, 1이면 수정됨)"),
                                fieldWithPath("data[].likeCount").description("댓글 좋아요 수"),
                                fieldWithPath("data[].liked").description("내가 좋아요 눌렀는지 여부"),
                                fieldWithPath("nextCursor").optional().description("다음 커서 (없으면 null)"),
                                fieldWithPath("hasNext").description("다음 페이지 존재 여부")
                        )
                ));

        verify(commentService, times(1)).getComments(eq(postId), any(), eq(cursor), eq(size));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 생성")
    void createComments() throws Exception {
        CommentRequest request = new CommentRequest("테스트 댓글");

        CommentAnchorResponse response = new CommentAnchorResponse(commentId, "","comment-"+commentId);

        when(commentService.createComment(eq(postId), any(CommentRequest.class), eq(1L)))
                .thenReturn(response);

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
                                fieldWithPath("commentId").description("댓글 ID"),
                                fieldWithPath("anchor").description("프론트에서 사용하는 앵커 ex. comment-{id}")
                        )
                ));

        verify(commentService).createComment(eq(postId), any(CommentRequest.class), eq(1L));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 수정")
    void modifyComment() throws Exception {
        CommentRequest request = new CommentRequest("수정된 댓글 내용");

        CommentAnchorResponse response = new CommentAnchorResponse(commentId, "","comment-" + commentId);

        when(commentService.modifyComment(eq(postId), eq(commentId), any(CommentRequest.class), eq(1L)))
                .thenReturn(response);

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
                                fieldWithPath("commentId").description("댓글 ID"),
                                fieldWithPath("anchor").description("프론트에서 사용하는 앵커 ex. comment-{id}")
                        )
                ));
        verify(commentService).modifyComment(eq(postId), eq(commentId), any(CommentRequest.class), eq(1L));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 삭제")
    void deleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(postId, commentId, 1L);

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

        verify(commentService).deleteComment(postId, commentId, 1L);
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 좋아요")
    void createLikeComment() throws Exception {
        doNothing().when(commentService).createLikeComment(commentId, 1L);

        mockMvc.perform(post("/posts/{postId}/comments/{commentId}/like", postId, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )
                ));

        verify(commentService).createLikeComment(commentId, 1L);
    }

    @Test
    @WithMockUserInfo
    @DisplayName("댓글 좋아요 취소")
    void deleteLikeComment() throws Exception {
        doNothing().when(commentService).deleteLikeComment(commentId, 1L);

        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}/like", postId, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )
                ));

        verify(commentService).deleteLikeComment(commentId, 1L);
    }

}
