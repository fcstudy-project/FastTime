package com.fasttime.domain.bootcamp.service;

import com.fasttime.domain.bootcamp.dto.request.CertificationRequestDTO;
import com.fasttime.domain.bootcamp.entity.Certification;
import com.fasttime.domain.bootcamp.entity.CertificationStatus;
import com.fasttime.domain.bootcamp.exception.CertificationBadRequestException;
import com.fasttime.domain.bootcamp.exception.CertificationNotFoundException;
import com.fasttime.domain.bootcamp.exception.CertificationUnAuthException;
import com.fasttime.domain.bootcamp.repository.CertificationRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final MemberRepository memberRepository;

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
}
