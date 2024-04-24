package com.fasttime.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ApplyToStudyRequestDto(
    @NotBlank
    String message
) {

}
