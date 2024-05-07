package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.entity.StudyApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyApplicationCustomRepository {

    Page<StudyApplication> findAllByConditions(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto,
        Pageable pageable
    );
}
