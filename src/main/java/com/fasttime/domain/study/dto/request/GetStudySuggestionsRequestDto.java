package com.fasttime.domain.study.dto.request;

import lombok.Builder;

@Builder
public record GetStudySuggestionsRequestDto(
    long receiverId,
    Long studyId
) {

}
