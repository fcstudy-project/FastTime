package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudyApplication;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record StudyApplicationsResponseDto(
    int totalPages,
    Boolean isLastPage,
    long totalStudyApplications,
    List<StudyApplicationDetailsResponseDto> studyApplications
) {

    public static StudyApplicationsResponseDto of(Page<StudyApplication> studyApplicationPage) {
        List<StudyApplicationDetailsResponseDto> studyApplications = new ArrayList<>();
        studyApplicationPage.forEach(studyApplication ->
            studyApplications.add(StudyApplicationDetailsResponseDto.of(studyApplication)));
        return StudyApplicationsResponseDto.builder()
            .totalPages(studyApplicationPage.getTotalPages())
            .isLastPage(studyApplicationPage.isLast())
            .totalStudyApplications(studyApplicationPage.getTotalElements())
            .studyApplications(studyApplications)
            .build();
    }
}
