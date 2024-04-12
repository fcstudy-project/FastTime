package com.fasttime.domain.certification.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectionRequestDTO(
    @NotBlank(message = "거절 사유는 반드시 입력해야 합니다.") String rejectionReason
) {

}
