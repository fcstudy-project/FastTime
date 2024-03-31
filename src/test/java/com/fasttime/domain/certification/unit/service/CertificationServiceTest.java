package com.fasttime.domain.certification.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.certification.dto.request.CertificationRequestDTO;
import com.fasttime.domain.certification.dto.response.AllCertificationResponseDTO;
import com.fasttime.domain.certification.entity.BootCamp;
import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;
import com.fasttime.domain.certification.exception.CertificationBadRequestException;
import com.fasttime.domain.certification.exception.CertificationNotFoundException;
import com.fasttime.domain.certification.exception.CertificationUnAuthException;
import com.fasttime.domain.certification.repository.BootCampRepository;
import com.fasttime.domain.certification.repository.CertificationRepository;
import com.fasttime.domain.certification.service.CertificationService;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.member.service.AdminService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CertificationServiceTest {

    @InjectMocks
    private CertificationService certificationService;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private BootCampRepository bootCampRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AdminService adminService;
    private BootCamp bootCamp1;
    private BootCamp bootCamp2;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        bootCamp1 = BootCamp.builder()
            .id(1L)
            .name("패스트캠퍼스X야놀자 부트캠프")
            .description("부트캠프1 설명")
            .image("Image1")
            .governmentFunded(true)
            .organizer("패스트캠퍼스")
            .website("Website1")
            .course("백엔드 코스")
            .build();

        bootCamp2 = BootCamp.builder()
            .id(2L)
            .name("Bootcamp2")
            .description("부트캠프2 설명")
            .image("Image2")
            .governmentFunded(false)
            .organizer("프로그래머스")
            .website("Website2")
            .course("프론트엔드 코스")
            .build();

        member1 = Member.builder()
            .id(1L)
            .email("user1@example.com")
            .nickname("User1")
            .password("password1")
            .role(Role.ROLE_USER)
            .build();

        member2 = Member.builder()
            .id(2L)
            .email("user2@example.com")
            .nickname("User2")
            .password("password2")
            .role(Role.ROLE_ADMIN)
            .build();
    }

    private Certification createCertification(Long id, Member member, String bootcampName,
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

    @Nested
    @DisplayName("createCertification()는 ")
    class Context_createCertification {

        @Test
        @DisplayName("인증 요청에 성공한다.")
        void new_create_willSuccess() {
            // given
            CertificationRequestDTO requestDto = new CertificationRequestDTO("bootcampName",
                "image",
                "content");
            Member member = new Member();
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
            given(certificationRepository.save(any(Certification.class))).willAnswer(
                invocation -> invocation.getArgument(0));

            // when
            Certification createdCertification = certificationService.createCertification(
                requestDto,
                1L);

            // then
            assertThat(createdCertification).isNotNull();
            assertThat(createdCertification.getBootcampName()).isEqualTo(requestDto.bootcampName());
            assertThat(createdCertification.getImage()).isEqualTo(requestDto.image());
            assertThat(createdCertification.getContent()).isEqualTo(requestDto.content());
            verify(memberRepository, times(1)).findById(anyLong());
            verify(certificationRepository, times(1)).save(any(Certification.class));
        }

        @Test
        @DisplayName("없는 회원의 요청일 경우 실패한다.")
        void NotFound_willFail() {
            // given
            CertificationRequestDTO requestDto = new CertificationRequestDTO("bootcampName",
                "image",
                "content");
            given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThrows(MemberNotFoundException.class,
                () -> certificationService.createCertification(requestDto, 1L));
            verify(memberRepository, times(1)).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("withdrawCertification()는 ")
    class Context_withdrawCertification {

        @Test
        @DisplayName("인증 요청 철회에 성공한다.")
        void withdraw_willSuccess() {
            // given
            Certification certification = createCertification(1L, member1, "Bootcamp1",
                CertificationStatus.APPROVED,
                "Image1", "Content1", bootCamp1, null, null);

            given(certificationRepository.findById(anyLong())).willReturn(
                Optional.of(certification));
            given(certificationRepository.save(any(Certification.class))).willAnswer(
                invocation -> invocation.getArgument(0));

            // when
            Certification withdrawnCertification = certificationService.withdrawCertification(1L,
                member1.getId(), "Reason");

            // then
            assertThat(withdrawnCertification).isNotNull();
            assertThat(withdrawnCertification.getStatus()).isEqualTo(CertificationStatus.WITHDRAW);
            assertThat(withdrawnCertification.getWithdrawalReason()).isEqualTo("Reason");
            verify(certificationRepository, times(1)).findById(anyLong());
            verify(certificationRepository, times(1)).save(any(Certification.class));
        }

        @Test
        @DisplayName("없는 인증일 경우 실패한다.")
        void NotFound_willFail() {
            // given
            given(certificationRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThrows(CertificationNotFoundException.class,
                () -> certificationService.withdrawCertification(1L, 1L, "Reason"));
        }

        @Test
        @DisplayName("권한이 없는 경우 실패한다.")
        void UnAuth_willFail() {
            // given
            Certification certification = new Certification(1L, "Bootcamp1", member1, "Image1",
                "Content1", null, null, CertificationStatus.APPROVED, null);
            given(certificationRepository.findById(anyLong())).willReturn(
                Optional.of(certification));

            // when & then
            assertThrows(CertificationUnAuthException.class,
                () -> certificationService.withdrawCertification(1L, 3L, "Reason"));
        }
    }

    @Nested
    @DisplayName("cancelWithdrawal()는 ")
    class Context_cancelWithdrawal {

        @Test
        @DisplayName("철회 취소를 성공한다.")
        void cancelWithdrawal_willSuccess() {
            // given
            Certification withdrawnCertification = createCertification(1L, member1, "Bootcamp Test",
                CertificationStatus.WITHDRAW, "ImageTest", "ContentTest", bootCamp1,
                "철회 사유", null);

            withdrawnCertification.softDelete();

            given(certificationRepository.findById(1L)).willReturn(
                Optional.of(withdrawnCertification));
            given(certificationRepository.save(any(Certification.class))).willReturn(
                withdrawnCertification);

            // when
            Certification restoredCertification = certificationService.cancelWithdrawal(1L,
                member1.getId());

            // then
            assertThat(restoredCertification).isNotNull();
            assertThat(restoredCertification.getStatus()).isEqualTo(CertificationStatus.PENDING);
            assertThat(restoredCertification.getWithdrawalReason()).isNull();
            verify(certificationRepository, times(1)).findById(1L);
            verify(certificationRepository, times(1)).save(any(Certification.class));
        }

        @Test
        @DisplayName("없는 인증일 경우 실패한다.")
        void NotFound_willFail() {
            // given
            given(certificationRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThrows(CertificationNotFoundException.class,
                () -> certificationService.cancelWithdrawal(1L, 1L));
        }

        @Test
        @DisplayName("철회된 인증서가 아닐경우 실패한다.")
        void BadRequest_willFail() {
            // given
            Certification approvedCertification = createCertification(1L, member1, "Bootcamp Test",
                CertificationStatus.APPROVED, "ImageTest", "ContentTest", bootCamp1,
                "철회 사유", null);

            given(certificationRepository.findById(anyLong())).willReturn(
                Optional.of(approvedCertification));

            // when & then
            assertThrows(CertificationBadRequestException.class,
                () -> certificationService.cancelWithdrawal(1L, 1L));
        }
    }

    @Test
    @DisplayName("인증서 승인 성공")
    void testApproveCertificationSuccess() {
        // given
        given(adminService.isAdmin(anyLong())).willReturn(true);

        Certification certification = createCertification(1L, member1,
            "Bootcamp Test", CertificationStatus.PENDING, null, null, null, null, null);

        given(certificationRepository.findById(anyLong())).willReturn(Optional.of(certification));
        given(bootCampRepository.findById(anyLong())).willReturn(Optional.of(bootCamp1));
        given(certificationRepository.save(any(Certification.class))).willAnswer(
            invocation -> invocation.getArgument(0));

        // when
        Certification approvedCertification = certificationService.approveCertification(1L,
            bootCamp1.getId(), member1.getId());

        // then
        assertThat(approvedCertification).isNotNull();
        assertThat(approvedCertification.getStatus()).isEqualTo(CertificationStatus.APPROVED);
        verify(certificationRepository, times(1)).findById(anyLong());
        verify(bootCampRepository, times(1)).findById(anyLong());
        verify(certificationRepository, times(1)).save(any(Certification.class));
    }

    @Test
    @DisplayName("인증서 거절 성공")
    void testRejectCertificationSuccess() {
        // given
        given(adminService.isAdmin(anyLong())).willReturn(true);

        Certification certification = createCertification(1L, member1, "Bootcamp Test",
            CertificationStatus.PENDING, null, null, null, null, null);

        given(certificationRepository.findById(anyLong())).willReturn(Optional.of(certification));
        given(certificationRepository.save(any(Certification.class))).willAnswer(
            invocation -> invocation.getArgument(0));

        // when
        Certification rejectedCertification = certificationService.rejectCertification(1L, 1L,
            "거절 사유");

        // then
        assertThat(rejectedCertification).isNotNull();
        assertThat(rejectedCertification.getStatus()).isEqualTo(CertificationStatus.REJECTED);
        assertThat(rejectedCertification.getRejectionReason()).isEqualTo("거절 사유");
        verify(certificationRepository, times(1)).findById(anyLong());
        verify(certificationRepository, times(1)).save(any(Certification.class));
    }


    @Test
    @DisplayName("상태별 인증서 조회")
    void testGetAllCertificationsByStatus() {
        // given
        Certification certification1 = new Certification(1L, "Bootcamp1", member1, "Image1",
            "Content1", null, bootCamp1, CertificationStatus.PENDING, null);
        Certification certification2 = new Certification(2L, "Bootcamp2", member2, "Image2",
            "Content2", null, bootCamp2, CertificationStatus.PENDING, null);

        Page<Certification> certificationPage = new PageImpl<>(
            List.of(certification1, certification2));
        given(certificationRepository.findByStatus(any(CertificationStatus.class),
            any(Pageable.class))).willReturn(certificationPage);

        // when
        Page<AllCertificationResponseDTO> results = certificationService.getAllCertificationsByStatus(
            CertificationStatus.PENDING, Pageable.unpaged());

        // then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(2);
        verify(certificationRepository, times(1)).findByStatus(any(CertificationStatus.class),
            any(Pageable.class));
    }
}
