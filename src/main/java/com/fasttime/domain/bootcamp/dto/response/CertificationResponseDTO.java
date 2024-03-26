package com.fasttime.domain.bootcamp.dto.response;

import com.fasttime.domain.bootcamp.entity.Certification;
import com.fasttime.domain.bootcamp.entity.CertificationStatus;

public record CertificationResponseDTO(
    Long memberId,
    CertificationStatus status,
    String bootcampName,
    String content,
    String image
) {

    public static CertificationResponseDTO from(Certification certification) {
        return new CertificationResponseDTO(
            certification.getMember().getId(),
            certification.getStatus(),
            certification.getBootcampName(),
            certification.getContent(),
            certification.getImage()
        );
    }
}
