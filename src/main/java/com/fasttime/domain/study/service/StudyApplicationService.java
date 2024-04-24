package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.response.ApplyToStudyResponseDto;

public interface StudyApplicationService {

    ApplyToStudyResponseDto apply(
        long applicantId,
        long studyId,
        ApplyToStudyRequestDto applyToStudyRequestDto
    );
}
