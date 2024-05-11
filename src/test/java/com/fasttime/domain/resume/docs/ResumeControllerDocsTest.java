package com.fasttime.domain.resume.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.domain.resume.controller.ResumeController;
import com.fasttime.domain.resume.dto.LikeResumeRequest;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumeUpdateServiceRequest;
import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.service.ResumeService;
import com.fasttime.global.util.SecurityUtil;
import com.fasttime.docs.RestDocsSupport;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ResumeControllerDocsTest extends RestDocsSupport {

    private ResumeService resumeService;
    private SecurityUtil securityUtil;

    private MockMvc mockMvc;

    @Override
    public Object initController() {
        resumeService = mock(ResumeService.class);
        securityUtil = mock(SecurityUtil.class);
        return new ResumeController(securityUtil, resumeService);
    }

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
            .standaloneSetup(initController())
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @DisplayName("자기소개서 작성 API 문서화")
    @Test
    public void createResume() throws Exception {
        String resumeJson = "{\"title\":\"자기소개서 제목 예시\",\"content\":\"자기소개서 내용 예시\"}";

        mockMvc.perform(post("/api/v2/resumes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resumeJson))
            .andExpect(status().isCreated())
            .andDo(document("create-resume",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("자기소개서 제목"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("자기소개서 내용")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data").description("자기소개서 상세 정보")
                )
            ));
    }

    @DisplayName("자기소개서 삭제 API 문서화")
    @Test
    public void deleteResume() throws Exception {
        Long resumeId = 1L;

        mockMvc.perform(delete("/api/v2/resumes/{resumeId}", resumeId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("delete-resume",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("resumeId").description("삭제할 자기소개서의 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터").type(JsonFieldType.NULL)
                )
            ));
    }

    @DisplayName("자기소개서 수정 API 문서화")
    @Test
    public void updateResume() throws Exception {
        Long resumeId = 1L;
        String updateJson = "{\"title\":\"수정 제목\",\"content\":\"수정 내용\"}";

        ResumeResponseDto resumeResponseDto = new ResumeResponseDto(resumeId,
            "수정 제목", "수정 내용", "김아무개", 10, 100, LocalDateTime.now(),
            LocalDateTime.now(), null);
        when(resumeService.updateResume(any(ResumeUpdateServiceRequest.class))).thenReturn(
            resumeResponseDto);

        mockMvc.perform(put("/api/v2/resumes/{resumeId}", resumeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andDo(document("update-resume",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("resumeId").description("수정할 자기소개서의 ID")
                ),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("자기소개서 제목"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("자기소개서 내용")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("자기소개서 ID"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("자기소개서 제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING)
                        .description("자기소개서 내용"),
                    fieldWithPath("data.writer").type(JsonFieldType.STRING)
                        .description("자기소개서 작성자"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING)
                        .description("마지막 수정 시간"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 시간")
                )
            ));
    }

    @DisplayName("자기소개서 상세 조회 API 문서화")
    @Test
    public void getResume() throws Exception {
        Long resumeId = 1L;
        ResumeResponseDto resumeResponseDto = new ResumeResponseDto(
            resumeId,
            "자기소개서 제목 예시",
            "자기소개서 내용 예시",
            "김아무개",
            5,
            100,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );
        when(resumeService.getResume(eq(resumeId), anyString())).thenReturn(resumeResponseDto);

        mockMvc.perform(get("/api/v2/resumes/{resumeId}", resumeId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-resume",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("resumeId").description("자기소개서의 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("응답 메시지"),
                    subsectionWithPath("data").description("자기소개서 상세 정보")
                        .type(JsonFieldType.OBJECT),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("자기소개서 ID"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("자기소개서 제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING)
                        .description("자기소개서 내용"),
                    fieldWithPath("data.writer").type(JsonFieldType.STRING)
                        .description("자기소개서 작성자"),
                    fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 수"),
                    fieldWithPath("data.viewCount").type(JsonFieldType.NUMBER).description("조회 수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                    fieldWithPath("data.lastModifiedAt").type(JsonFieldType.STRING)
                        .description("마지막 수정 시간"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 시간 (삭제된 경우)")
                )
            ));
    }

    @DisplayName("자기소개서 전체 조회 API 문서화")
    @Test
    public void getResumes() throws Exception {
        List<ResumeResponseDto> resumes = List.of(
            new ResumeResponseDto(1L, "자소서 1", "내용 1", "김아무개", 3, 150,
                LocalDateTime.now(), LocalDateTime.now(), null),
            new ResumeResponseDto(2L, "자소서 2", "내용 2", "최아무개", 5, 100,
                LocalDateTime.now(), LocalDateTime.now(), null)
        );

        when(resumeService.search(any(ResumesSearchRequest.class))).thenReturn(resumes);

        mockMvc.perform(get("/api/v2/resumes")
                .param("pageSize", "10")
                .param("page", "0")
                .param("orderBy", "date")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("get-resumes",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("pageSize").description("페이지당 항목 수").optional(),
                    parameterWithName("page").description("페이지 번호").optional(),
                    parameterWithName("orderBy").description("정렬 기준").optional()
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).optional()
                        .description("응답 메시지"),
                    subsectionWithPath("data").description("자기소개서 리스트").type(JsonFieldType.ARRAY),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("자기소개서 ID"),
                    fieldWithPath("data[].title").type(JsonFieldType.STRING)
                        .description("자기소개서 제목"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING)
                        .description("자기소개서 내용"),
                    fieldWithPath("data[].writer").type(JsonFieldType.STRING)
                        .description("자기소개서 작성자"),
                    fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER)
                        .description("좋아요 수"),
                    fieldWithPath("data[].viewCount").type(JsonFieldType.NUMBER)
                        .description("조회 수"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
                        .description("생성 시간"),
                    fieldWithPath("data[].lastModifiedAt").type(JsonFieldType.STRING)
                        .description("마지막 수정 시간"),
                    fieldWithPath("data[].deletedAt").type(JsonFieldType.STRING).optional()
                        .description("삭제 시간 (삭제된 경우)")
                )
            ));
    }

    @DisplayName("자기소개서 좋아요 추가 API 문서화")
    @Test
    public void likeResume() throws Exception {
        Long resumeId = 1L;

        doNothing().when(resumeService).likeResume(any(LikeResumeRequest.class));

        mockMvc.perform(post("/api/v2/resumes/{resumeId}/likes", resumeId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("like-resume",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("resumeId").description("자기소개서의 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터")
                        .type(JsonFieldType.NULL)
                )
            ));
    }

    @DisplayName("자기소개서 좋아요 취소 API 문서화")
    @Test
    public void cancelLikeResume() throws Exception {
        Long resumeId = 1L;

        doNothing().when(resumeService).cancelLike(any(LikeResumeRequest.class));

        mockMvc.perform(delete("/api/v2/resumes/{resumeId}/likes", resumeId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("cancel-like-resume",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("resumeId").description("자기소개서의 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data").description("응답 데이터")
                        .type(JsonFieldType.NULL)
                )
            ));
    }
}
