package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.SuggestStudyResponseDto;

public interface StudySuggestionService {

    SuggestStudyResponseDto suggest(
        long memberId,
        long receiverId,
        long studyId,
        SuggestStudyRequestDto suggestStudyRequestDto
    );
}
