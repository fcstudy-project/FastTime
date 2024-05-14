package com.fasttime.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 스터디 참여 제안 요청 DTO
 *
 * @param message 스터디 참여 제안 메시지
 */
public record SuggestStudyRequestDto(
    @NotBlank
    String message
) {

}
