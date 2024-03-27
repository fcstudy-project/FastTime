package com.fasttime.domain.bootcamp.dto.response;

import com.fasttime.domain.bootcamp.entity.CertificationStatus;

public record AllCertificationResponseDTO(
    Long certificationId,
    CertificationStatus status,
    long memberId,
    String requestedBootcampName,
    String assignedBootcampName,
    String image,
    String content,
    String withdrawalReason,
    String rejectionReason

) {

}
