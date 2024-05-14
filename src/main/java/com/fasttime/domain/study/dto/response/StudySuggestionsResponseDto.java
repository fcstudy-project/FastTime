package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudySuggestion;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

/**
 * 스터디 참여 제안 목록 응답 DTO
 *
 * @param totalPages            스터디 참여 제안 목록 총 페이지 수
 * @param isLastPage            마지막 페이지 여부
 * @param totalStudySuggestions 스터디 참여 제안 총 개수
 * @param studySuggestions      스터디 참여 제안 목록
 */
@Builder
public record StudySuggestionsResponseDto(
    int totalPages,
    boolean isLastPage,
    long totalStudySuggestions,
    List<StudySuggestionDetailsResponseDto> studySuggestions
) {

    /**
     * 스터디 참여 제안 페이지를 스터디 참여 제안 목록 응답 DTO로 변환하는 정적 팩토리 메서드
     *
     * @param studySuggestionPage 스터디 참여 제안 페이지 객체
     * @return 스터디 참여 제안 목록 응답 DTO
     */
    public static StudySuggestionsResponseDto of(Page<StudySuggestion> studySuggestionPage) {
        List<StudySuggestionDetailsResponseDto> studySuggestions = new ArrayList<>();
        for (StudySuggestion studySuggestion : studySuggestionPage) {
            studySuggestions.add(StudySuggestionDetailsResponseDto.of(studySuggestion));
        }
        return StudySuggestionsResponseDto.builder()
            .totalPages(studySuggestionPage.getTotalPages())
            .isLastPage(studySuggestionPage.isLast())
            .totalStudySuggestions(studySuggestionPage.getTotalElements())
            .studySuggestions(studySuggestions)
            .build();
    }
}
