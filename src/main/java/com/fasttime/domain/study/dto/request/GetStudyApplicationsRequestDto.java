package com.fasttime.domain.study.dto.request;

import lombok.Builder;

@Builder
public record GetStudyApplicationsRequestDto(
    long applicantId,
    Long studyId
) {

}
