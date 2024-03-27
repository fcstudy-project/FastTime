package com.fasttime.domain.bootcamp.dto.response;

import com.fasttime.domain.bootcamp.entity.Certification;

public record ApproveResponseDTO(
    Long memberId,
    Long certificationId,
    String status,
    String bootcampName
) {
    public static ApproveResponseDTO from(Certification certification) {
        Long memberId = certification.getMember().getId();
        Long certificationId = certification.getId();
        String status = certification.getStatus().name();
        String bootcampName = certification.getBootCamp() != null ? certification.getBootCamp().getName() : null;

        return new ApproveResponseDTO(memberId, certificationId, status, bootcampName);
    }
}
