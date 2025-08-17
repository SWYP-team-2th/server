package com.chooz.user.presentation;

import com.chooz.support.RestDocsTest;
import com.chooz.support.WithMockUserInfo;
import com.chooz.user.domain.OnboardingStepType;
import com.chooz.user.presentation.dto.OnboardingRequest;
import com.chooz.user.presentation.dto.UserInfoResponse;
import com.chooz.user.presentation.dto.UserMyInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends RestDocsTest {

    @Test
    @WithMockUserInfo
    @DisplayName("유저 정보 조회")
    void findUserInfo() throws Exception {
        //given
        Map<String, Boolean> onboardingStep = Map.of(
                "WELCOME_GUIDE", false,
                "FIRST_VOTE", true
        );
        UserInfoResponse response = new UserInfoResponse(
                1L,
                "nickname",
                "https://cdn.chooz.site/default_profile.png",
                false,
                onboardingStep
        );
        given(userService.findById(1L))
                .willReturn(response);
        System.out.println(objectMapper.writeValueAsString(response));
        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/{userId}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("userId").description("유저 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("유저 아이디")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("nickname")
                                        .description("닉네임")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("profileImageUrl")
                                        .description("프로필 이미지 URL")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("notification")
                                        .description("알림 설정 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep")
                                        .description("유저 온보딩 단계")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("onboardingStep.WELCOME_GUIDE")
                                        .description("웰컴 가이드 완료 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep.FIRST_VOTE")
                                        .description("첫 투표 완료 여부")
                                        .type(JsonFieldType.BOOLEAN)
                        )
                ));
    }

    @Test
    @WithMockUserInfo
    @DisplayName("본인 정보 조회")
    void findMe() throws Exception {
        //given
        Map<String, Boolean> onboardingStep = Map.of(
                "WELCOME_GUIDE", false,
                "FIRST_VOTE", true
        );
        UserMyInfoResponse response = new UserMyInfoResponse(
                1L,
                "nickname",
                "https://cdn.chooz.site/default_profile.png",
                false,
                onboardingStep
        );
        given(userService.findByMe(1L))
                .willReturn(response);

        //when then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("id")
                                        .description("유저 아이디")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("nickname")
                                        .description("닉네임")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("profileImageUrl")
                                        .description("프로필 이미지 URL")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("notification")
                                        .description("알림 설정 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep")
                                        .description("유저 온보딩 단계")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("onboardingStep.WELCOME_GUIDE")
                                        .description("웰컴 가이드 완료 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep.FIRST_VOTE")
                                        .description("첫 투표 완료 여부")
                                        .type(JsonFieldType.BOOLEAN)
                        )
                ));
    }
    @Test
    @WithMockUserInfo
    @DisplayName("온보딩 수행")
    void completeStep () throws Exception {
        // given
        Map<OnboardingStepType, Boolean> steps = Map.of(
                OnboardingStepType.WELCOME_GUIDE, false,
                OnboardingStepType.FIRST_VOTE, true

        );
        OnboardingRequest request = new OnboardingRequest(steps);

        Map<String, Boolean> responseSteps = Map.of(
                "WELCOME_GUIDE", false,
                "FIRST_VOTE", true

        );
        UserInfoResponse response = new UserInfoResponse(
                1L,
                "nickname",
                "https://cdn.chooz.site/default_profile.png",
                false,
                responseSteps
        );

        given(userService.completeStep(eq(1L), any(OnboardingRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/users/onboarding")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("onboardingStep")
                                        .description("온보딩 단계")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("onboardingStep.WELCOME_GUIDE")
                                        .description("웰컴 가이드 완료 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep.FIRST_VOTE")
                                        .description("첫 투표 완료 여부")
                                        .type(JsonFieldType.BOOLEAN)
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("유저 아이디")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("nickname")
                                        .description("닉네임")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("profileImageUrl")
                                        .description("프로필 이미지 URL")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("notification")
                                        .description("알림 설정 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep")
                                        .description("유저 온보딩 단계")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("onboardingStep.WELCOME_GUIDE")
                                        .description("웰컴 가이드 완료 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("onboardingStep.FIRST_VOTE")
                                        .description("첫 투표 완료 여부")
                                        .type(JsonFieldType.BOOLEAN)
                        )
                ));
    }
}
