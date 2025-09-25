package com.chooz.notification.presentation;

import com.chooz.common.dto.CursorBasePaginatedResponse;
import com.chooz.notification.domain.Actor;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Receiver;
import com.chooz.notification.domain.Target;
import com.chooz.notification.domain.TargetType;
import com.chooz.notification.presentation.dto.NotificationResponse;
import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotificationControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("알림 목록 조회")
    void findNotifications() throws Exception {
        //given
        var response = new CursorBasePaginatedResponse<>(
                1L,
                false,
                List.of(
                        new NotificationResponse(
                                1L,
                                2L,
                                new Actor(2L, "공개된 츄", "https://cdn.chooz.site/default_profile.png"),
                                NotificationType.COMMENT_LIKED,
                                List.of(Target.of(1L, TargetType.POST)),
                                "https://cdn.chooz.site/images/20865b3c-4e2c-454a-81a1-9ca31bbaf77d",
                                true,
                                false,
                                LocalDateTime.now()
                        )
                )
        );
        given(notificationQueryService.findNotifications(1L, null, 10)).willReturn(response);

        //when then
        mockMvc.perform(get("/notifications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestHeaders(authorizationHeader()),
                        queryParameters(cursorQueryParams()),
                        responseFields(
                                fieldWithPath("nextCursor").type(JsonFieldType.NUMBER).optional()
                                        .description("다음 조회 커서 값"),
                                fieldWithPath("hasNext")
                                        .type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부 (기본 값 10)"),
                                fieldWithPath("data[]")
                                        .type(JsonFieldType.ARRAY).description("알림 데이터"),
                                fieldWithPath("data[].id")
                                        .type(JsonFieldType.NUMBER).description("알림 ID"),
                                fieldWithPath("data[].receiverId")
                                        .type(JsonFieldType.NUMBER).description("receiver ID"),
                                fieldWithPath("data[].actor.id")
                                        .type(JsonFieldType.NUMBER).description("actor ID"),
                                fieldWithPath("data[].actor.nickname")
                                        .type(JsonFieldType.STRING).description("actor 닉네임"),
                                fieldWithPath("data[].actor.profileUrl")
                                        .type(JsonFieldType.STRING).description("actor 프로필 이미지 url"),
                                fieldWithPath("data[].notificationType")
                                        .type(JsonFieldType.STRING).description("알림 유형"),
                                fieldWithPath("data[].targets[].id")
                                        .type(JsonFieldType.NUMBER).description("알림 타겟 ID"),
                                fieldWithPath("data[].targets[].type")
                                        .type(JsonFieldType.STRING).description("알림 타겟 유형"),
                                fieldWithPath("data[].imageUrl")
                                        .type(JsonFieldType.STRING).description("알림 썸네일 이미지 url"),
                                fieldWithPath("data[].isValid")
                                        .type(JsonFieldType.BOOLEAN).description("알림 상태"),
                                fieldWithPath("data[].isRead")
                                        .type(JsonFieldType.BOOLEAN).description("읽음 여부"),
                                fieldWithPath("data[].eventAt")
                                        .type(JsonFieldType.STRING).description("이벤트 발생 시간")
                        )
                ));
    }
}
