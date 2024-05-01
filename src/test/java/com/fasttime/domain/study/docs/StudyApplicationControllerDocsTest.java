package com.fasttime.domain.study.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.study.controller.StudyApplicationController;
import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.service.StudyApplicationService;
import com.fasttime.domain.study.service.StudyApplicationServiceImpl;
import com.fasttime.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;

public class StudyApplicationControllerDocsTest extends RestDocsSupport {

    private final StudyApplicationService studyApplicationService = mock(
        StudyApplicationServiceImpl.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public StudyApplicationController initController() {
        return new StudyApplicationController(securityUtil, studyApplicationService);
    }

    @DisplayName("apply()는 스터디 참여를 지원할 수 있다.")
    @Test
    public void apply() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(new ApplyToStudyRequestDto("스터디 같이 해요!"));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        given(studyApplicationService.apply(
            any(long.class),
            any(long.class),
            any(ApplyToStudyRequestDto.class)
        )).willReturn(new StudyApplicationResponseDto(1L));

        // when then
        mockMvc.perform(post("/api/v2/studies/{studyId}", 1L)
                .session(session)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("study-apply",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("message").type(JsonFieldType.STRING).description("신청 메시지")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.studyApplicationId").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 신청 식별자"))));
    }

    @DisplayName("approve()는 스터디 참여 지원을 승인할 수 있다.")
    @Test
    public void approve() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        given(studyApplicationService.approve(
            any(long.class),
            any(long.class)
        )).willReturn(new StudyApplicationResponseDto(1L));

        // when then
        mockMvc.perform(patch("/api/v2/studies/{studyId}/applications/{studyApplicationId}", 1L, 1L)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("study-approve",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("studyId").description("스터디 식별자"),
                    parameterWithName("studyApplicationId").description("수락할 스터디 참여 신청 식별자")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.studyApplicationId").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 신청 식별자"))));
    }

    @DisplayName("reject()는 스터디 참여 신청을 거부할 수 있다.")
    @Test
    public void reject() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        given(studyApplicationService.reject(
            any(long.class),
            any(long.class)
        )).willReturn(new StudyApplicationResponseDto(1L));

        // when then
        mockMvc.perform(
                delete("/api/v2/studies/{studyId}/applications/{studyApplicationId}", 1L, 1L)
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("study-reject",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("studyId").description("스터디 식별자"),
                    parameterWithName("studyApplicationId").description("거부할 스터디 참여 신청 식별자")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.studyApplicationId").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 신청 식별자"))));
    }
}
