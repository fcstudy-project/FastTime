package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.entity.StudySuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudySuggestionCustomRepository {

    Page<StudySuggestion> findAllByConditions(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto,
        Pageable pageable
    );
}
