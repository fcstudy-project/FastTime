package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudySuggestion;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record StudySuggestionsResponseDto(
    int totalPages,
    Boolean isLastPage,
    long totalStudySuggestions,
    List<StudySuggestionDetailsResponseDto> studySuggestions
) {

    public static StudySuggestionsResponseDto of(Page<StudySuggestion> studySuggestionPage) {
        List<StudySuggestionDetailsResponseDto> studySuggestions = new ArrayList<>();
        studySuggestionPage.forEach(studySuggestion ->
            studySuggestions.add(StudySuggestionDetailsResponseDto.of(studySuggestion)));
        return StudySuggestionsResponseDto.builder()
            .totalPages(studySuggestionPage.getTotalPages())
            .isLastPage(studySuggestionPage.isLast())
            .totalStudySuggestions(studySuggestionPage.getTotalElements())
            .studySuggestions(studySuggestions)
            .build();
    }
}
