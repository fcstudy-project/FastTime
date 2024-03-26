package com.fasttime.domain.bootcamp.dto.response;

import com.fasttime.domain.bootcamp.entity.Certification;
import com.fasttime.domain.bootcamp.entity.CertificationStatus;

public record MyCertificationResponseDTO(
    Long certificationId,
    String bootcampName,
    CertificationStatus status,
    String withdrawalReason
) {

    public static MyCertificationResponseDTO from(Certification certification) {
        return new MyCertificationResponseDTO(
            certification.getId(),
            certification.getBootcampName(),
            certification.getStatus(),
            certification.getWithdrawalReason()
        );
    }
}
