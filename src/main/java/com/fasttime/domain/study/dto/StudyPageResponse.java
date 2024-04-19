package com.fasttime.domain.study.dto;

import com.fasttime.domain.study.entity.Study;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record StudyPageResponse(
    int totalPages,
    Boolean isLastPage,
    long totalStudies,
    List<StudyResponse> studies
) {

    public static StudyPageResponse of(Page<Study> studyPage, List<StudyResponse> studies) {
        return StudyPageResponse.builder()
            .totalPages(studyPage.getTotalPages())
            .isLastPage(studyPage.isLast())
            .totalStudies(studyPage.getTotalElements())
            .studies(studies)
            .build();
    }
}
