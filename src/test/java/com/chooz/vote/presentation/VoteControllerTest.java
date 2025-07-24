package com.chooz.vote.presentation;

import com.chooz.vote.presentation.dto.VoteStatusResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import com.chooz.vote.presentation.dto.VoteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("투표")
    void vote() throws Exception {
        //given
        VoteRequest request = new VoteRequest(1L, List.of(1L));

        //when test
        mockMvc.perform(post("/votes", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        requestFields(
                                fieldWithPath("postId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("게시글 Id"),
                                fieldWithPath("pollChoiceIds")
                                        .type(JsonFieldType.ARRAY)
                                        .description("투표 선택지 Id")
                        )
                ));
        verify(voteService, times(1)).vote(any(), any(), any());
    }

    @Test
    @WithMockUserInfo
    @DisplayName("게시글 투표 상태 조회")
    void findVoteStatus() throws Exception {
        //given
        var response = List.of(
                new VoteStatusResponse(1L, "title1", "http://example.com/image/1", 2, "66.7"),
                new VoteStatusResponse(2L, "title2", "http://example.com/image/2", 1, "33.3")
        );
        given(voteService.findVoteStatus(1L, 1L))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}/votes/status", 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        pathParameters(
                                parameterWithName("postId").description("게시글 Id")
                        ),
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("투표 선택지 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("이미지 Id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("사진 이름"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("사진 URL"),
                                fieldWithPath("[].voteCount").type(JsonFieldType.NUMBER).description("투표 수"),
                                fieldWithPath("[].voteRatio").type(JsonFieldType.STRING).description("투표 비율")
                        )
                ));
    }
}
