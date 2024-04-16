package com.fasttime.domain.certification.docs;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.docs.RestDocsSupport;
import com.fasttime.domain.certification.controller.CertificationController;
import com.fasttime.domain.certification.dto.request.CertificationRequestDTO;
import com.fasttime.domain.certification.dto.request.RejectionRequestDTO;
import com.fasttime.domain.certification.dto.request.WithdrawalRequestDTO;
import com.fasttime.domain.certification.dto.response.AllCertificationResponseDTO;
import com.fasttime.domain.certification.entity.BootCamp;
import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;
import com.fasttime.domain.certification.service.CertificationService;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.global.util.SecurityUtil;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CertificationControllerDocsTest extends RestDocsSupport {

    private CertificationService certificationService;
    private SecurityUtil securityUtil;

    public static Member createMember(Long id, String email, String nickname, String password,
        Role role) {
        return Member.builder()
            .id(id)
            .email(email)
            .nickname(nickname)
            .password(password)
            .role(role)
            .build();
    }

    public static Certification createCertification(Long id, Member member, String bootcampName,
        CertificationStatus status, String image, String content,
        BootCamp bootCamp, String withdrawalReason, String rejectionReason) {
        return Certification.builder()
            .id(id)
            .member(member)
            .bootcampName(bootcampName)
            .status(status)
            .image(image)
            .content(content)
            .bootCamp(bootCamp)
            .withdrawalReason(withdrawalReason)
            .rejectionReason(rejectionReason)
            .build();
    }

    @Override
    public Object initController() {
        certificationService = mock(CertificationService.class);
        securityUtil = mock(SecurityUtil.class);
        return new CertificationController(certificationService, securityUtil);
    }

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
            .standaloneSetup(initController())
            .apply(documentationConfiguration(restDocumentation))
            .build();

    }

    @DisplayName("인증 요청 생성 API 문서화")
    @Test
    void createCertification() throws Exception {

        CertificationRequestDTO requestDTO = new CertificationRequestDTO(
            "패스트캠퍼스X야놀자 부트캠프 백엔드 코스(member가 자유롭게 작성 가능)",
            "http://image.url",
            "인증 수락 해주세요!"
        );

        Member member = createMember(1L, "user@example.com", "김아무개", "pass123", Role.ROLE_USER);

        Certification certification = createCertification(1L, member, requestDTO.bootcampName(),
            CertificationStatus.PENDING, requestDTO.image(), requestDTO.content(), null, null,
            null);

        when(
            certificationService.createCertification(any(CertificationRequestDTO.class), anyLong()))
            .thenReturn(certification);

        when(securityUtil.getCurrentMemberId()).thenReturn(1L);

        mockMvc.perform(post("/api/v2/certification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andDo(document("certification-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("bootcampName").type(JsonFieldType.STRING)
                        .description("인증 받고 싶은 부트캠프 이름"),
                    fieldWithPath("image").type(JsonFieldType.STRING)
                        .description("이미지 URL(ex.수료증 사진)"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("인증서 본문")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                        .description("요청 멤버 ID"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("인증 진행 상태"),
                    fieldWithPath("data.bootcampName").type(JsonFieldType.STRING)
                        .description("인증 받고 싶은 부트캠프 이름"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING)
                        .description("인증서 본문"),
                    fieldWithPath("data.image").type(JsonFieldType.STRING)
                        .description("이미지 URL(ex.수료증 사진)")
                )
            ));
    }

    @DisplayName("인증 철회 API 문서화")
    @Test
    void withdrawCertification() throws Exception {
        Long certificationId = 1L;
        Long memberId = 1L;
        String withdrawalReason = "이미지를 잘못 첨부해 인증 철회합니다.";

        WithdrawalRequestDTO withdrawalRequestDTO = new WithdrawalRequestDTO(withdrawalReason);

        Member member = createMember(memberId, "user@example.com", "김아무개", "pass123",
            Role.ROLE_USER);
        Certification certification = createCertification(certificationId, member,
            "패스트캠퍼스X야놀자 부트캠프 백엔드 코스(member가 자유롭게 작성 가능)",
            CertificationStatus.WITHDRAW, "http://image.url", "인증 수락 해주세요!", null, withdrawalReason,
            null);

        when(
            certificationService.withdrawCertification(certificationId, memberId, withdrawalReason))
            .thenReturn(certification);

        when(securityUtil.getCurrentMemberId()).thenReturn(memberId);

        mockMvc.perform(post("/api/v2/certification/withdraw/{certificationId}", certificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawalRequestDTO)))
            .andExpect(status().isOk())
            .andDo(document("certification-withdraw",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("certificationId").description("인증서 ID")
                ),
                requestFields(
                    fieldWithPath("withdrawalReason").type(JsonFieldType.STRING)
                        .description("철회 사유")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                        .description("멤버 ID"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("인증 진행 상태"),
                    fieldWithPath("data.bootcampName").type(JsonFieldType.STRING)
                        .description("인증 받고 싶은 부트캠프 이름"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING)
                        .description("인증서 본문"),
                    fieldWithPath("data.image").type(JsonFieldType.STRING)
                        .description("이미지 URL(ex.수료증 사진)")
                )
            ));
    }

    @DisplayName("내 인증 요청 조회 API 문서화")
    @Test
    void getMyCertifications() throws Exception {
        Long currentMemberId = 1L;
        List<Certification> certifications = List.of(
            createCertification(1L, null, "패스트캠퍼스X야놀자 부트캠프 백엔드 코스(member가 자유롭게 작성 가능)",
                CertificationStatus.PENDING,
                "http://image.url", "내용1", null, null, null),
            createCertification(2L, null, "다른 부트캠프(member가 자유롭게 작성 가능)",
                CertificationStatus.APPROVED,
                "http://image2.url", "내용2", null, "철회 사유", null)
        );

        when(securityUtil.getCurrentMemberId()).thenReturn(currentMemberId);
        when(certificationService.getCertificationsByMember(currentMemberId)).thenReturn(
            certifications);

        mockMvc.perform(get("/api/v2/certification/my-certifications"))
            .andExpect(status().isOk())
            .andDo(document("certification-get-my",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data[].certificationId").type(JsonFieldType.NUMBER)
                        .description("인증서 ID"),
                    fieldWithPath("data[].bootcampName").type(JsonFieldType.STRING)
                        .description("인증 받고 싶은 부트캠프 이름"),
                    fieldWithPath("data[].status").type(JsonFieldType.STRING)
                        .description("인증 진행 상태"),
                    fieldWithPath("data[].withdrawalReason").type(JsonFieldType.STRING)
                        .description("철회 사유").optional(),
                    fieldWithPath("data[].rejectionReason").type(JsonFieldType.STRING)
                        .description("거절 사유").optional()
                )
            ));
    }

    @DisplayName("인증 철회 취소 API 문서화")
    @Test
    void cancelWithdrawal() throws Exception {
        Long certificationId = 1L;
        Long currentMemberId = 1L;

        Member member = createMember(1L, "user@example.com", "김아무개", "pass123", Role.ROLE_USER);
        Certification certification = createCertification(1L, member,
            "패스트캠퍼스X야놀자 부트캠프 백엔드 코스(member가 자유롭게 작성 가능)",
            CertificationStatus.PENDING, "imageURL", "content", null, null, null);

        when(securityUtil.getCurrentMemberId()).thenReturn(currentMemberId);
        when(certificationService.cancelWithdrawal(certificationId, currentMemberId)).thenReturn(
            certification);

        mockMvc.perform(
                patch("/api/v2/certification/cancel-withdrawal/{certificationId}", certificationId))
            .andExpect(status().isOk())
            .andDo(document("certification-cancel-withdrawal",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("certificationId").description("인증서 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER)
                        .description("멤버 ID"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING)
                        .description("인증 진행 상태"),
                    fieldWithPath("data.bootcampName").type(JsonFieldType.STRING)
                        .description("인증 받고 싶은 부트캠프 이름"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING)
                        .description("인증서 본문"),
                    fieldWithPath("data.image").type(JsonFieldType.STRING)
                        .description("이미지 URL(ex.수료증 사진)")
                )
            ));
    }

    @DisplayName("인증 수락 API 문서화")
    @Test
    void approveCertification() throws Exception {
        Long certificationId = 1L;
        Long bootCampId = 1L;
        Long currentMemberId = 1L;

        Member member = CertificationControllerDocsTest.createMember(currentMemberId,
            "user@example.com", "김아무개", "pass123", Role.ROLE_USER);
        BootCamp bootCamp = new BootCamp(bootCampId, "패스트캠퍼스X야놀자 부트캠프(admin이 부여)", "Description",
            "Image URL", true,
            "Organizer1", "Website1", "Course");
        Certification certification = CertificationControllerDocsTest.createCertification(
            certificationId, member, "Bootcamp", CertificationStatus.APPROVED, "imageURL",
            "content",
            bootCamp, null, null);

        when(securityUtil.getCurrentMemberId()).thenReturn(currentMemberId);
        when(certificationService.approveCertification(certificationId, bootCampId,
            currentMemberId)).thenReturn(certification);

        mockMvc.perform(
                patch("/api/v2/certification/approve/{certificationId}/{bootCampId}", certificationId,
                    bootCampId))
            .andExpect(status().isOk())
            .andDo(document("certification-approve",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("certificationId").description("인증서 ID"),
                    parameterWithName("bootCampId").description("부트캠프 ID(admin 부여)")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                    fieldWithPath("data.certificationId").type(JsonFieldType.NUMBER)
                        .description("인증서 ID"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("인증 진행 상태"),
                    fieldWithPath("data.bootcampName").type(JsonFieldType.STRING)
                        .description("admin이 부여한 부트캠프 이름")
                )
            ));
    }

    @DisplayName("인증 거절 API 문서화")
    @Test
    void rejectCertification() throws Exception {
        Long certificationId = 1L;
        Long currentAdminId = 1L;
        String rejectionReason = "부적절한 내용으로 인증 거절";

        Member member = CertificationControllerDocsTest.createMember(1L, "user@example.com", "김아무개",
            "pass123", Role.ROLE_ADMIN);
        Certification certification = CertificationControllerDocsTest.createCertification(
            certificationId, member, "패스트캠퍼스X야놀자 부트캠프 백엔드 코스(member가 자유롭게 작성 가능)",
            CertificationStatus.REJECTED, "imageURL", "content",
            null, null, "부적절한 내용으로 인증 거절");

        when(securityUtil.getCurrentMemberId()).thenReturn(currentAdminId);
        when(certificationService.rejectCertification(certificationId, currentAdminId,
            rejectionReason)).thenReturn(certification);

        mockMvc.perform(patch("/api/v2/certification/reject/{certificationId}", certificationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RejectionRequestDTO(rejectionReason))))
            .andExpect(status().isOk())
            .andDo(document("certification-reject",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("certificationId").description("인증서 ID")
                ),
                requestFields(
                    fieldWithPath("rejectionReason").type(JsonFieldType.STRING).description("거절 사유")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.certificationId").type(JsonFieldType.NUMBER)
                        .description("인증서 ID"),
                    fieldWithPath("data.bootcampName").type(JsonFieldType.STRING)
                        .description("인증 받고 싶은 부트캠프 이름"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("인증 진행 상태"),
                    fieldWithPath("data.withdrawalReason").type(JsonFieldType.STRING)
                        .description("철회 사유").optional(),
                    fieldWithPath("data.rejectionReason").type(JsonFieldType.STRING)
                        .description("거절 사유")
                )
            ));
    }

    @DisplayName("인증서 전체 조회 API 문서화")
    @Test
    void getAllCertifications() throws Exception {
        AllCertificationResponseDTO dto = new AllCertificationResponseDTO(
            1L, CertificationStatus.PENDING, 2L, "member가 요청한 부트캠프 이름", "admin이 부여한 부트캠프 이름",
            "Image URL", "Content", null, null
        );
        when(certificationService.getAllCertificationsByStatus(any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(dto)));

        mockMvc.perform(get("/api/v2/certification")
                .param("status", "PENDING")
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("certifications-get-all",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("status").optional().description("인증 진행 상태"),
                    parameterWithName("page").optional().description("페이지 번호")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 상태 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    subsectionWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.certification[].certificationId").type(JsonFieldType.NUMBER)
                        .description("인증서 ID"),
                    fieldWithPath("data.certification[].status").type(JsonFieldType.STRING)
                        .description("인증 진행 상태"),
                    fieldWithPath("data.certification[].memberId").type(JsonFieldType.NUMBER)
                        .description("멤버 ID"),
                    fieldWithPath("data.certification[].requestedBootcampName").type(
                        JsonFieldType.STRING).description("요청된 부트캠프 이름(member가 요청)"),
                    fieldWithPath("data.certification[].assignedBootcampName").type(
                        JsonFieldType.STRING).description("할당된 부트캠프 이름(admin이 부여)"),
                    fieldWithPath("data.certification[].image").type(JsonFieldType.STRING)
                        .description("이미지 URL"),
                    fieldWithPath("data.certification[].content").type(JsonFieldType.STRING)
                        .description("인증 내용"),
                    fieldWithPath("data.certification[].withdrawalReason").type(
                        JsonFieldType.STRING).optional().description("철회 사유"),
                    fieldWithPath("data.certification[].rejectionReason").type(JsonFieldType.STRING)
                        .optional().description("거절 사유"),
                    fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER)
                        .description("현재 페이지"),
                    fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                        .description("총 페이지 수"),
                    fieldWithPath("data.currentElements").type(JsonFieldType.NUMBER)
                        .description("현재 페이지의 인증서 수"),
                    fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                        .description("전체 인증서 수")
                )
            ));
    }
}
