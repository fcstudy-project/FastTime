package com.fasttime.domain.studyComment.docs;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.studyComment.controller.StudyCommentController;
import com.fasttime.domain.studyComment.dto.request.CreateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.request.GetStudyCommentsRequestDTO;
import com.fasttime.domain.studyComment.dto.request.UpdateStudyCommentRequestDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentListResponseDTO;
import com.fasttime.domain.studyComment.dto.response.StudyCommentResponseDTO;
import com.fasttime.domain.studyComment.service.StudyCommentService;
import com.fasttime.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudyCommentControllerDocsTest extends RestDocsSupport {

    private final StudyCommentService studyCommentService = mock(StudyCommentService.class);
    private final SecurityUtil securityUtil = mock(SecurityUtil.class);

    @Override
    public Object initController() {
        return new StudyCommentController(studyCommentService, securityUtil);
    }

    ConstraintDescriptions createCommentRequestConstraints = new ConstraintDescriptions(
        CreateStudyCommentRequestDTO.class);
    ConstraintDescriptions updateCommentRequestConstraints = new ConstraintDescriptions(
        UpdateStudyCommentRequestDTO.class);

    @DisplayName("스터디 댓글 등록 API 문서화")
    @Test
    void createComment() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(CreateStudyCommentRequestDTO.builder()
            .content("얼마나 걸리셨나요?")
            .parentStudyCommentId(null)
            .build());


        given(studyCommentService.createComment(any(long.class), any(long.class),
            any(CreateStudyCommentRequestDTO.class))).willReturn(
            StudyCommentResponseDTO.builder()
                .commentId(1L)
                .studyId(1L)
                .memberId(1L)
                .nickname("깜찍이")
                .content("얼마나 걸리셨나요?")
                .parentStudyCommentId(-1L)
                .childStudyCommentCount(0)
                .createdAt("2024-01-01 12:00:00")
                .updatedAt(null)
                .deletedAt(null)
                .build());

        // when, then
        mockMvc.perform(post("/api/v2/studyComments/{studyId}", 1L)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(document("studyComment-create",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        .attributes(key("constraints").value(
                            createCommentRequestConstraints.descriptionsForProperty("content"))),
                    fieldWithPath("parentStudyCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자").optional()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data.studyId").type(JsonFieldType.NUMBER)
                        .description("스터디 모집글 식별자"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data.parentStudyCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.childStudyCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시")
                        .optional(),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).description("삭제 일시")
                        .optional())));
    }

    @DisplayName("스터디 댓글 목록 조회 API 문서화")
    @Test
    void getComments() throws Exception {
        // given
        given(studyCommentService.getComments(any(GetStudyCommentsRequestDTO.class), any(Pageable.class)))
            .willReturn(StudyCommentListResponseDTO.builder()
                .totalPages(1)
                .isLastPage(true)
                .totalComments(6)
                .comments(
                    List.of(
                        StudyCommentResponseDTO.builder()
                            .commentId(1L)
                            .studyId(1L)
                            .memberId(1L)
                            .nickname("깜찍이")
                            .content("얼마나 걸리셨나요?")
                            .parentStudyCommentId(-1L)
                            .childStudyCommentCount(2)
                            .createdAt("2024-01-01 12:00:00")
                            .updatedAt(null)
                            .deletedAt(null)
                            .build(),
                            StudyCommentResponseDTO.builder()
                            .commentId(3L)
                            .studyId(1L)
                            .memberId(3L)
                            .nickname("멋쟁이")
                            .content("오...")
                            .parentStudyCommentId(-1L)
                            .childStudyCommentCount(0)
                            .createdAt("2024-01-01 14:00:00")
                            .updatedAt(null)
                            .deletedAt(null)
                            .build(),
                            StudyCommentResponseDTO.builder()
                            .commentId(4L)
                            .studyId(1L)
                            .memberId(3L)
                            .nickname("잼민이")
                            .content("굿")
                            .parentStudyCommentId(-1L)
                            .createdAt("2024-01-01 14:30:00")
                            .childStudyCommentCount(1)
                            .updatedAt(null)
                            .deletedAt(null)
                            .build()))
                .build());

        // when, then
        mockMvc.perform(get("/api/v2/studyComments")
                .queryParam("studyId", "1")
                .queryParam("page", "0")
                .queryParam("pageSize", "10"))
            .andExpect(status().isOk())
            .andDo(document("studyComments-search",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("studyId").description("스터디 모집글 식별자").optional(),
                    parameterWithName("memberId").description("회원 식별자").optional(),
                    parameterWithName("parentStudyCommentId").description("대댓글을 조회할 댓글 식별자").optional(),
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
                    fieldWithPath("data.totalComments").type(JsonFieldType.NUMBER)
                        .description("총 댓글 수"),
                    fieldWithPath("data.comments").type(JsonFieldType.ARRAY).description("댓글 목록"),
                    fieldWithPath("data.comments[].commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data.comments[].studyId").type(JsonFieldType.NUMBER)
                        .description("스터디 모집글 식별자"),
                    fieldWithPath("data.comments[].memberId").type(JsonFieldType.NUMBER)
                        .description("회원 식별자"),
                    fieldWithPath("data.comments[].nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.comments[].content").type(JsonFieldType.STRING)
                        .description("댓글 내용"),
                    fieldWithPath("data.comments[].parentStudyCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.comments[].childStudyCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.comments[].createdAt").type(JsonFieldType.STRING)
                        .description("등록 일시"),
                    fieldWithPath("data.comments[].updatedAt").type(JsonFieldType.STRING)
                        .description("수정 일시").optional(),
                    fieldWithPath("data.comments[].deletedAt").type(JsonFieldType.STRING)
                        .description("삭제 일시").optional())));
    }

    @DisplayName("스터디 댓글 수정 API 문서화")
    @Test
    void articleUpdate() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(UpdateStudyCommentRequestDTO.builder()
            .content("얼마나 걸리셨을까요?")
            .build());
        given(studyCommentService.updateComment(any(long.class), any(long.class),
            any(UpdateStudyCommentRequestDTO.class)))
            .willReturn(StudyCommentResponseDTO.builder()
                .commentId(1L)
                .studyId(1L)
                .memberId(1L)
                .nickname("깜찍이")
                .content("얼마나 걸리셨을까요?")
                .parentStudyCommentId(-1L)
                .childStudyCommentCount(2)
                .createdAt("2023-10-01 12:00:00")
                .updatedAt("2023-10-01 12:30:00")
                .deletedAt(null)
                .build());

        // when, then
        mockMvc.perform(patch("/api/v2/studyComments/{studyCommentId}", 1L)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("studyComment-update",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        .attributes(key("constraints").value(
                            updateCommentRequestConstraints.descriptionsForProperty("content")))),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data.studyId").type(JsonFieldType.NUMBER)
                        .description("스터디 모집글 식별자"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data.parentStudyCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.childStudyCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING).description("삭제 일시")
                        .optional())));
    }

    @DisplayName("게시글 삭제 API 문서화")
    @Test
    void articleDelete() throws Exception {
        // given
        given(studyCommentService.deleteComment(any(long.class), any(long.class))).willReturn(
            StudyCommentResponseDTO.builder()
                .commentId(1L)
                .studyId(1L)
                .memberId(1L)
                .nickname("깜찍이")
                .content("얼마나 걸리셨을까요?")
                .parentStudyCommentId(-1L)
                .childStudyCommentCount(2)
                .createdAt("2024-10-01 12:00:00")
                .updatedAt("2024-10-01 12:30:00")
                .deletedAt("2024-10-01 21:00:00")
                .build());

        // when, then
        mockMvc.perform(delete("/api/v2/studyComments/{studyCommentId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("studyComment-delete",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답데이터"),
                    fieldWithPath("data.commentId").type(JsonFieldType.NUMBER)
                        .description("댓글 식별자"),
                    fieldWithPath("data.studyId").type(JsonFieldType.NUMBER)
                        .description("스터디 모집글 식별자"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING)
                        .description("작성자 닉네임"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data.parentStudyCommentId").type(JsonFieldType.NUMBER)
                        .description("부모 댓글 식별자"),
                    fieldWithPath("data.childStudyCommentCount").type(JsonFieldType.NUMBER)
                        .description("대댓글 개수"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("등록 일시"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                    fieldWithPath("data.deletedAt").type(JsonFieldType.STRING)
                        .description("삭제 일시"))));
    }
}
