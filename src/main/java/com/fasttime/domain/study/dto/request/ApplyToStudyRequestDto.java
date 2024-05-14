package com.fasttime.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 스터디 참여 신청 요청 DTO
 *
 * @param message 스터디 참여 신청 메시지
 */
public record ApplyToStudyRequestDto(
    @NotBlank
    String message
) {

}
