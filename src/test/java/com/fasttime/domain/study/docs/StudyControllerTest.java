package com.fasttime.domain.study.docs;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.study.controller.StudyController;
import com.fasttime.domain.study.dto.StudyCreateRequest;
import com.fasttime.domain.study.dto.StudyPageResponse;
import com.fasttime.domain.study.dto.StudyResponse;
import com.fasttime.domain.study.dto.StudyUpdateRequest;
import com.fasttime.domain.study.service.StudyService;
import com.fasttime.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.snippet.Attributes;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudyControllerTest extends RestDocsSupport {
    private final StudyService studyService = mock(StudyService.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new StudyController(studyService, securityUtil);
    }

    @DisplayName("스터디 모집글 작성 API")
    @Test
    void creatStudy() throws Exception {
        //given
        StudyCreateRequest studyCreateRequest = new StudyCreateRequest(
                "test 스터티 제목", "test 스터디 본문", "파이썬 자바", 10,
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(3), "010-0000-0000", List.of(1L, 2L));
        //when, then
        when(studyService.createStudy(anyLong(), any(StudyCreateRequest.class)))
                .thenReturn(StudyResponse.builder()
                        .id(1L).nickname("부캠러")
                        .title("Test 스터디 제목")
                        .content("Test 스터디 본문")
                        .skill("파이썬 자바")
                        .recruitmentStart(LocalDate.now())
                        .recruitmentEnd(LocalDate.now().plusMonths(1))
                        .progressStart(LocalDate.now().plusMonths(1))
                        .progressEnd(LocalDate.now().plusMonths(3))
                        .total(10)
                        .applicant(0)
                        .current(0)
                        .categories(List.of("면접 준비", "알고리즘"))
                        .contact("010-0000-0000").build());

        mockMvc.perform(post("/api/v2/studies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyCreateRequest)))
                .andExpect(status().isCreated())
                .andDo(document("study-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("skill").type(JsonFieldType.STRING).description("필요 스택"),
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("recruitmentEnd").type(JsonFieldType.STRING).description("모집 종료 날짜"),
                                fieldWithPath("progressStart").type(JsonFieldType.STRING).description("스터디 시작 날짜"),
                                fieldWithPath("progressEnd").type(JsonFieldType.STRING).description("스터디 종료 날짜"),
                                fieldWithPath("contact").type(JsonFieldType.STRING).description("연락처"),
                                fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).optional()
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING).description("생성된 스터디 모집글 조회 URL")
                        )
                ));
    }

    @DisplayName("스터디 모집글 수정 API")
    @Test
    void updateStudy() throws Exception {

        //given
        StudyUpdateRequest studyUpdateRequest = new StudyUpdateRequest(
                "test 스터티 제목", "test 스터디 본문", "파이썬 자바", 10,
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(3), "010-0000-0000", List.of(1L, 2L));

        //when then
        when(studyService.updateStudy(anyLong(), anyLong(), any(StudyUpdateRequest.class)))
                .thenReturn(StudyResponse.builder()
                        .id(1L).nickname("부캠러")
                        .title("Test 스터디 제목")
                        .content("Test 스터디 본문")
                        .skill("파이썬 자바")
                        .recruitmentStart(LocalDate.now())
                        .recruitmentEnd(LocalDate.now().plusMonths(1))
                        .progressStart(LocalDate.now().plusMonths(1))
                        .progressEnd(LocalDate.now().plusMonths(3))
                        .total(10)
                        .applicant(0)
                        .current(0)
                        .categories(List.of("면접 준비", "알고리즘"))
                        .contact("010-0000-0000").build());

        mockMvc.perform(put("/api/v2/studies/{study_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyUpdateRequest)))
                .andExpect(status().isOk())
                .andDo(document("study-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("study_id").description("삭제할 스터디 모집글 식별자")),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("skill").type(JsonFieldType.STRING).description("필요 역량"),
                                fieldWithPath("total").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("recruitmentEnd").type(JsonFieldType.STRING).description("모집 종료 일시"),
                                fieldWithPath("progressStart").type(JsonFieldType.STRING).description("스터디 시작 일시"),
                                fieldWithPath("progressEnd").type(JsonFieldType.STRING).description("스터디 종료 일시"),
                                fieldWithPath("contact").type(JsonFieldType.STRING).description("연락처"),
                                fieldWithPath("categoryIds").type(JsonFieldType.ARRAY).description("스터디 카테고리 식별자 리스트")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                                fieldWithPath("message").type(JsonFieldType.NULL).description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("수정된 스터디 모집글 ID")
                        )
                ));
    }

    @DisplayName("스터디 모집글 삭제 API")
    @Test
    void deleteStudy() throws Exception {
        //given
        doNothing().when(studyService).deleteStudy(anyLong(), anyLong());

        //when, then
        mockMvc.perform(delete("/api/v2/studies/{study_id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("study-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("study_id").description("삭제할 스터디 모집글 식별자"))
                ));
    }

    @DisplayName("스터디 모집글 상세 조회 API ")
    @Test
    void searchStudy() throws Exception {
        //given
        when(studyService.getStudy(anyLong()))
                .thenReturn(StudyResponse.builder()
                        .id(1L).nickname("부캠러")
                        .title("Test 스터디 제목")
                        .content("Test 스터디 본문")
                        .skill("파이썬 자바")
                        .recruitmentStart(LocalDate.now())
                        .recruitmentEnd(LocalDate.now().plusMonths(1))
                        .progressStart(LocalDate.now().plusMonths(1))
                        .progressEnd(LocalDate.now().plusMonths(3))
                        .total(10)
                        .applicant(0)
                        .current(0)
                        .categories(List.of("면접 준비", "알고리즘"))
                        .contact("010-0000-0000").build());

        //when //then
        mockMvc.perform(get("/api/v2/studies/{study_id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("study-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("study_id").description("스터디 모집글 식별자")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).optional()
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("스터디 모집글 식별자"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("스터디 모집글 제목"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("스터디 모집글 본문"),
                                fieldWithPath("data.skill").type(JsonFieldType.STRING)
                                        .description("필요 역량"),
                                fieldWithPath("data.total").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("data.current").type(JsonFieldType.NUMBER).description("현재 인원"),
                                fieldWithPath("data.applicant").type(JsonFieldType.NUMBER).description("지원자 수"),
                                fieldWithPath("data.recruitmentStart").type(JsonFieldType.STRING).description("모집 시작 일시"),
                                fieldWithPath("data.recruitmentEnd").type(JsonFieldType.STRING).description("모집 종료 일시"),
                                fieldWithPath("data.progressStart").type(JsonFieldType.STRING).description("스터디 시작 일시"),
                                fieldWithPath("data.progressEnd").type(JsonFieldType.STRING).description("스터디 종료 일시"),
                                fieldWithPath("data.contact").type(JsonFieldType.STRING).description("연락처"),
                                fieldWithPath("data.categories").type(JsonFieldType.ARRAY).description("스터디 카테고리 ")
                        )));
    }

    @DisplayName("스터디 모집글 목록 조회 API ")
    @Test
    void searchStudies() throws Exception {

        //given
        StudyResponse studyResponse1 = StudyResponse.builder()
                .id(1L).nickname("부캠러")
                .title("Test 스터디 제목1")
                .content("Test 스터디 본문1")
                .skill("파이썬 자바")
                .recruitmentStart(LocalDate.now())
                .recruitmentEnd(LocalDate.now().plusMonths(1))
                .progressStart(LocalDate.now().plusMonths(1))
                .progressEnd(LocalDate.now().plusMonths(3))
                .total(10)
                .applicant(0)
                .current(0)
                .categories(List.of("면접 준비", "알고리즘"))
                .contact("010-0000-0000").build();
        StudyResponse studyResponse2 = StudyResponse.builder()
                .id(1L).nickname("부캠러")
                .title("Test 스터디 제목2")
                .content("Test 스터디 본문2")
                .skill("파이썬 자바 c++")
                .recruitmentStart(LocalDate.now())
                .recruitmentEnd(LocalDate.now().plusMonths(1))
                .progressStart(LocalDate.now().plusMonths(1))
                .progressEnd(LocalDate.now().plusMonths(3))
                .total(5)
                .applicant(0)
                .current(0)
                .categories(List.of("면접 준비", "프로젝트"))
                .contact("010-0000-0000").build();

        List<StudyResponse> studyResponses = List.of(studyResponse1, studyResponse2);

        when(studyService.searchStudies(any()))
                .thenReturn(StudyPageResponse.builder()
                        .studies(studyResponses)
                        .totalStudies(2)
                        .totalPages(1)
                        .isLastPage(true).build());

        //when then
        mockMvc.perform(get("/api/v2/studies")
                        .queryParam("page", "0")
                        .queryParam("pageSize", "10")
                        .queryParam("orderBy", "latest")
                )
                .andExpect(status().isOk())
                .andDo(document("study-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("pageSize").description("조회당 불러올 건 수").optional()
                                        .attributes(new Attributes.Attribute("defaultValue", "10")),
                                parameterWithName("page").description("조회 페이지").optional()
                                        .attributes(new Attributes.Attribute("defaultValue", "0")),
                                parameterWithName("orderBy").description(
                                                "정렬 기준 : latest(default), applicant").optional()
                                        .attributes(new Attributes.Attribute("constraints",
                                                        "latest : 최신 순 \n applicant : 지원자 수"),
                                                new Attributes.Attribute("defaultValue", "latest"))
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).optional()
                                        .description("메시지"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("data.isLastPage").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지"),
                                fieldWithPath("data.totalStudies").type(JsonFieldType.NUMBER)
                                        .description("스터디 모집글 총 개수"),
                                fieldWithPath("data.studies").type(JsonFieldType.ARRAY)
                                        .description("스터디 모집글 리스트"),
                                fieldWithPath("data.studies[].id").type(JsonFieldType.NUMBER).description("스터디 모집글 식별자"),
                                fieldWithPath("data.studies[].title").type(JsonFieldType.STRING).description("스터디 모집글 제목"),
                                fieldWithPath("data.studies[].nickname").type(JsonFieldType.STRING)
                                        .description("작성자 닉네임"),
                                fieldWithPath("data.studies[].content").type(JsonFieldType.STRING).description("스터디 모집글 본문"),
                                fieldWithPath("data.studies[].skill").type(JsonFieldType.STRING)
                                        .description("필요 역량"),
                                fieldWithPath("data.studies[].total").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("data.studies[].current").type(JsonFieldType.NUMBER).description("현재 인원"),
                                fieldWithPath("data.studies[].applicant").type(JsonFieldType.NUMBER).description("지원자 수"),
                                fieldWithPath("data.studies[].recruitmentStart").type(JsonFieldType.STRING).description("모집 시작 일시"),
                                fieldWithPath("data.studies[].recruitmentEnd").type(JsonFieldType.STRING).description("모집 종료 일시"),
                                fieldWithPath("data.studies[].progressStart").type(JsonFieldType.STRING).description("스터디 시작 일시"),
                                fieldWithPath("data.studies[].progressEnd").type(JsonFieldType.STRING).description("스터디 종료 일시"),
                                fieldWithPath("data.studies[].contact").type(JsonFieldType.STRING).description("연락처"),
                                fieldWithPath("data.studies[].categories").type(JsonFieldType.ARRAY).description("스터디 카테고리 ")
                        )
                ));
    }
}

