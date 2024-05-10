package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudySuggestion;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record StudySuggestionDetailsResponseDto(
    long id,
    String status,
    long studyId,
    long receiverId,
    String nickname,
    String message,
    String createdAt,
    String updatedAt,
    String deletedAt
) {

    public static StudySuggestionDetailsResponseDto of(StudySuggestion studySuggestion) {
        return StudySuggestionDetailsResponseDto.builder()
            .id(studySuggestion.getId())
            .status(studySuggestion.getStatus().name())
            .studyId(studySuggestion.getStudy().getId())
            .receiverId(studySuggestion.getReceiver().getId())
            .nickname(studySuggestion.getReceiver().getNickname())
            .message(studySuggestion.getMessage())
            .createdAt(dateTimeParse(studySuggestion.getCreatedAt()))
            .updatedAt(dateTimeParse(studySuggestion.getUpdatedAt()))
            .deletedAt(dateTimeParse(studySuggestion.getDeletedAt()))
            .build();
    }

    private static String dateTimeParse(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
