package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;

public interface StudyApplicationService {

    StudyApplicationResponseDto apply(
        long applicantId,
        long studyId,
        ApplyToStudyRequestDto applyToStudyRequestDto
    );

    StudyApplicationResponseDto approve(
        long memberId,
        long studyApplicationId
    );


}
