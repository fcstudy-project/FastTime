package com.fasttime.domain.study.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.study.controller.StudySuggestionController;
import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudySuggestionDetailsResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionsResponseDto;
import com.fasttime.domain.study.service.StudySuggestionService;
import com.fasttime.domain.study.service.StudySuggestionServiceImpl;
import com.fasttime.global.util.SecurityUtil;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;

public class StudySuggestionControllerDocsTest extends RestDocsSupport {

    private final StudySuggestionService studySuggestionService = mock(
        StudySuggestionServiceImpl.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public StudySuggestionController initController() {
        return new StudySuggestionController(securityUtil, studySuggestionService);
    }

    @DisplayName("suggestStudy()는 스터디 참여를 제안할 수 있다.")
    @Test
    public void suggestStudy() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(new ApplyToStudyRequestDto("스터디 같이 해요!"));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        given(studySuggestionService.suggest(
            any(long.class),
            any(long.class),
            any(long.class),
            any(SuggestStudyRequestDto.class)
        )).willReturn(new StudySuggestionResponseDto(1L));

        // when then
        mockMvc.perform(post("/api/v2/studies/{studyId}/members/{memberId}", 1L, 2L)
                .session(session)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("study-suggest",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("studyId").description("제안할 스터디 식별자"),
                    parameterWithName("memberId").description("제안 수신 회원 식별자")),
                requestFields(
                    fieldWithPath("message").type(JsonFieldType.STRING).description("신청 메시지")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.studySuggestionId").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 제안 식별자"))));
    }

    @DisplayName("getStudyApplications()는 스터디 참여 신청 목록을 조회할 수 있다.")
    @Test
    public void getStudyApplications() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);
        List<StudySuggestionDetailsResponseDto> studySuggestions = List.of(
            StudySuggestionDetailsResponseDto.builder()
                .id(1L)
                .status("CONSIDERING")
                .studyId(1L)
                .receiverId(2L)
                .nickname("닉네임2")
                .message("스터디 같이 해요!")
                .createdAt("2024-01-01 12:00:00")
                .updatedAt(null)
                .deletedAt(null)
                .build(),
            StudySuggestionDetailsResponseDto.builder()
                .id(2L)
                .status("CONSIDERING")
                .studyId(1L)
                .receiverId(3L)
                .nickname("닉네임3")
                .message("스터디 같이 하고 싶어요!")
                .createdAt("2024-01-01 12:30:00")
                .updatedAt(null)
                .deletedAt(null)
                .build()
        );
        StudySuggestionsResponseDto studyApplicationsResponseDto = StudySuggestionsResponseDto.builder()
            .totalPages(1)
            .isLastPage(true)
            .totalStudySuggestions(2)
            .studySuggestions(studySuggestions)
            .build();

        given(studySuggestionService.getStudySuggestions(
            any(GetStudySuggestionsRequestDto.class),
            any(PageRequest.class)
        )).willReturn(studyApplicationsResponseDto);

        // when then
        mockMvc.perform(get("/api/v2/studies/suggestions", 1L, 1L)
                .session(session)
                .queryParam("studyId", "1")
                .queryParam("page", "0")
                .queryParam("pageSize", "10"))
            .andExpect(status().isOk())
            .andDo(document("study-suggestions-get",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("studyId").description("스터디 참여 제안 목록을 불러올 스터디 식별자")
                        .optional(),
                    parameterWithName("pageSize").description("조회당 불러올 건 수").optional(),
                    parameterWithName("page").description("조회 페이지").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                        .description("총 페이지 수"),
                    fieldWithPath("data.isLastPage").type(JsonFieldType.BOOLEAN)
                        .description("마지막 페이지 여부"),
                    fieldWithPath("data.totalStudySuggestions").type(JsonFieldType.NUMBER)
                        .description("총 스터디 참여 제안 수"),
                    fieldWithPath("data.studySuggestions").type(JsonFieldType.ARRAY)
                        .description("스터디 참여 제안 목록"),
                    fieldWithPath("data.studySuggestions[].id").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 제안 식별자"),
                    fieldWithPath("data.studySuggestions[].status").type(JsonFieldType.STRING)
                        .description("스터디 참여 제안 상태"),
                    fieldWithPath("data.studySuggestions[].studyId").type(JsonFieldType.NUMBER)
                        .description("참여 제안한 스터디 식별자"),
                    fieldWithPath("data.studySuggestions[].receiverId").type(JsonFieldType.NUMBER)
                        .description("참여 제안 수신 회원 식별자"),
                    fieldWithPath("data.studySuggestions[].nickname").type(JsonFieldType.STRING)
                        .description("참여 제안 수신 회원 닉네임"),
                    fieldWithPath("data.studySuggestions[].message").type(JsonFieldType.STRING)
                        .description("스터디 참여 제안 메시지"),
                    fieldWithPath("data.studySuggestions[].createdAt").type(JsonFieldType.STRING)
                        .description("등록 일시"),
                    fieldWithPath("data.studySuggestions[].updatedAt").type(JsonFieldType.STRING)
                        .description("수정 일시").optional(),
                    fieldWithPath("data.studySuggestions[].deletedAt").type(JsonFieldType.STRING)
                        .description("삭제 일시").optional()
                )));

    }

    @DisplayName("approve()는 스터디 참여 제안을 승인할 수 있다.")
    @Test
    public void approve() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        given(studySuggestionService.approve(
            any(long.class),
            any(long.class)
        )).willReturn(new StudySuggestionResponseDto(1L));

        // when then
        mockMvc.perform(patch("/api/v2/studies/{studyId}/suggestions/{studySuggestionId}", 1L, 1L)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("study-suggestion-approve",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("studyId").description("스터디 식별자"),
                    parameterWithName("studySuggestionId").description("수락할 스터디 참여 제안 식별자")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.studySuggestionId").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 제안 식별자"))));
    }

    @DisplayName("reject()는 스터디 참여 제안을 거부할 수 있다.")
    @Test
    public void reject() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("MEMBER", 1L);

        given(studySuggestionService.reject(
            any(long.class),
            any(long.class)
        )).willReturn(new StudySuggestionResponseDto(1L));

        // when then
        mockMvc.perform(
                delete("/api/v2/studies/{studyId}/suggestions/{studySuggestionId}", 1L, 1L)
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("study-suggestion-reject",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("studyId").description("스터디 식별자"),
                    parameterWithName("studySuggestionId").description("거부할 스터디 참여 제안 식별자")),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.studySuggestionId").type(JsonFieldType.NUMBER)
                        .description("스터디 참여 제안 식별자"))));
    }
}
