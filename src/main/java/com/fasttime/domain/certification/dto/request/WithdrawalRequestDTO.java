package com.fasttime.domain.certification.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WithdrawalRequestDTO(
    @NotBlank(message = "철회 사유는 필수입니다.")
    String withdrawalReason
) {

}
