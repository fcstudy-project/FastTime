package com.fasttime.domain.study.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SuggestStudyRequestDto(
    @NotBlank
    String message
) {

}
