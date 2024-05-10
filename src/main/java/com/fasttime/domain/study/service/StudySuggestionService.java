package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionsResponseDto;
import org.springframework.data.domain.PageRequest;

public interface StudySuggestionService {

    StudySuggestionResponseDto suggest(
        long memberId,
        long receiverId,
        long studyId,
        SuggestStudyRequestDto suggestStudyRequestDto
    );

    StudySuggestionsResponseDto getStudySuggestions(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto,
        PageRequest pageRequest
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
