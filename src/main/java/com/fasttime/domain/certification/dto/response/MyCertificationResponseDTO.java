package com.fasttime.domain.certification.dto.response;

import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;

public record MyCertificationResponseDTO(
    Long certificationId,
    String bootcampName,
    CertificationStatus status,
    String withdrawalReason,
    String rejectionReason
) {

    public static MyCertificationResponseDTO from(Certification certification) {
        return new MyCertificationResponseDTO(
            certification.getId(),
            certification.getBootcampName(),
            certification.getStatus(),
            certification.getWithdrawalReason(),
            certification.getRejectionReason()
        );
    }
}
