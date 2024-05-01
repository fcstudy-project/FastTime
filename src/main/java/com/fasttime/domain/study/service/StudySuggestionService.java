package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;

public interface StudySuggestionService {

    StudySuggestionResponseDto suggest(
        long memberId,
        long receiverId,
        long studyId,
        SuggestStudyRequestDto suggestStudyRequestDto
    );

    StudySuggestionResponseDto approve(
        long memberId,
        long studySuggestionId
    );

    StudySuggestionResponseDto reject(
        long memberId,
        long studySuggestionId
    );
}
