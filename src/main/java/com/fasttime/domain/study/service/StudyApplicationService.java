package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.dto.response.StudyApplicationsResponseDto;
import org.springframework.data.domain.PageRequest;

public interface StudyApplicationService {

    StudyApplicationResponseDto apply(
        long applicantId,
        long studyId,
        ApplyToStudyRequestDto applyToStudyRequestDto
    );

    StudyApplicationsResponseDto getStudyApplications(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto,
        PageRequest pageRequest
    );

    StudyApplicationResponseDto approve(
        long memberId,
        long studyApplicationId
    );

    StudyApplicationResponseDto reject(
        long memberId,
        long studyApplicationId
    );
}
