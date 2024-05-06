package com.fasttime.domain.certification.service;

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
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.member.service.AdminService;
import com.fasttime.domain.review.exception.BootCampNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final BootCampRepository bootCampRepository;
    private final MemberRepository memberRepository;
    private final AdminService adminService;

    public Certification createCertification(CertificationRequestDTO requestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException());

        Certification certification = Certification.builder()
            .member(member)
            .bootcampName(requestDto.bootcampName())
            .image(requestDto.image())
            .content(requestDto.content())
            .status(CertificationStatus.PENDING)
            .build();

        return certificationRepository.save(certification);
    }

    public Certification withdrawCertification(Long certificationId, Long currentMemberId,
        String withdrawalReason) {
        Certification certification = certificationRepository.findById(certificationId)
            .orElseThrow(() -> new CertificationNotFoundException());

        if (!certification.getMember().getId().equals(currentMemberId)) {
            throw new CertificationUnAuthException();
        }

        certification.setWithdrawalReason(withdrawalReason);
        certification.setStatus(CertificationStatus.WITHDRAW);
        certification.softDelete();

        return certificationRepository.save(certification);
    }

    @Transactional(readOnly = true)
    public List<Certification> getCertificationsByMember(Long memberId) {
        return certificationRepository.findByMemberId(memberId);
    }

    public Certification cancelWithdrawal(Long certificationId, Long currentMemberId) {
        Certification certification = certificationRepository.findById(certificationId)
            .orElseThrow(() -> new CertificationNotFoundException());

        if (!certification.getMember().getId().equals(currentMemberId)) {
            throw new CertificationUnAuthException();
        }

        if (certification.isDeleted()) {
            certification.restore();
            certification.setStatus(CertificationStatus.PENDING);
            certification.setWithdrawalReason(null);
            return certificationRepository.save(certification);
        } else {
            throw new CertificationBadRequestException();
        }
    }

    public Certification approveCertification(Long certificationId, Long bootCampId,
        Long currentMemberId) {
        if (!adminService.isAdmin(currentMemberId)) {
            throw new CertificationUnAuthException();
        }

        Certification certification = certificationRepository.findById(certificationId)
            .orElseThrow(() -> new CertificationNotFoundException());
        BootCamp bootCamp = bootCampRepository.findById(bootCampId)
            .orElseThrow(() -> new BootCampNotFoundException());

        Member member = certification.getMember();
        if (member.getBootCamp() != null && member.getBootCamp().getId().equals(bootCampId)) {
            throw new CertificationBadRequestException("이미 동일한 부트캠프 ID가 부여되었습니다.");
        }

        certification.setBootCamp(bootCamp);
        certification.setStatus(CertificationStatus.APPROVED);
        member.approveCertification(bootCamp);

        return certificationRepository.save(certification);
    }

    public Certification rejectCertification(Long certificationId, Long adminId,
        String rejectionReason) {
        if (!adminService.isAdmin(adminId)) {
            throw new CertificationUnAuthException();
        }

        Certification certification = certificationRepository.findById(certificationId)
            .orElseThrow(() -> new CertificationNotFoundException());

        certification.setRejectionReason(rejectionReason);
        certification.setStatus(CertificationStatus.REJECTED);

        return certificationRepository.save(certification);
    }

    @Transactional(readOnly = true)
    public Page<AllCertificationResponseDTO> getAllCertificationsByStatus(
        CertificationStatus status, Pageable pageable) {
        Page<Certification> certificationPage;
        if (status == null) {
            certificationPage = certificationRepository.findAll(pageable);
        } else {
            certificationPage = certificationRepository.findByStatus(status, pageable);
        }
        return certificationPage.map(this::convertToDTO);
    }

    private AllCertificationResponseDTO convertToDTO(Certification certification) {
        Long memberId =
            certification.getMember() != null ? certification.getMember().getId() : null;
        String bootCampName =
            certification.getBootCamp() != null ? certification.getBootCamp().getName() : null;

        return new AllCertificationResponseDTO(
            certification.getId(),
            certification.getStatus(),
            memberId,
            certification.getBootcampName(),
            bootCampName,
            certification.getImage(),
            certification.getContent(),
            certification.getWithdrawalReason(),
            certification.getRejectionReason()
        );
    }
}
