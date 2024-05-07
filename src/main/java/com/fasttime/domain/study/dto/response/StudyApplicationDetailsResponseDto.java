package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudyApplication;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record StudyApplicationDetailsResponseDto(
    long id,
    String status,
    long studyId,
    long applicantId,
    String nickname,
    String message,
    String createdAt,
    String updatedAt,
    String deleteAt
) {

    public static StudyApplicationDetailsResponseDto of(StudyApplication studyApplication) {
        return StudyApplicationDetailsResponseDto.builder()
            .id(studyApplication.getId())
            .status(studyApplication.getStatus().name())
            .studyId(studyApplication.getStudy().getId())
            .applicantId(studyApplication.getApplicant().getId())
            .nickname(studyApplication.getApplicant().getNickname())
            .message(studyApplication.getMessage())
            .createdAt(dateTimeParse(studyApplication.getCreatedAt()))
            .updatedAt(dateTimeParse(studyApplication.getUpdatedAt()))
            .deleteAt(dateTimeParse(studyApplication.getDeletedAt()))
            .build();
    }

    private static String dateTimeParse(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
