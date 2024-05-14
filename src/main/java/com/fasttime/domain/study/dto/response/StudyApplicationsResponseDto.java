package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudyApplication;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

/**
 * 스터디 참여 신청 목록 응답 DTO
 *
 * @param totalPages             스터디 참여 신청 목록 총 페이지 수
 * @param isLastPage             마지막 페이지 여부
 * @param totalStudyApplications 스터디 참여 신청 총 개수
 * @param studyApplications      스터디 참여 신청 목록
 */
@Builder
public record StudyApplicationsResponseDto(
    int totalPages,
    Boolean isLastPage,
    long totalStudyApplications,
    List<StudyApplicationDetailsResponseDto> studyApplications
) {

    /**
     * 스터디 참여 신청 페이지를 스터디 참여 신청 목록 응답 DTO로 변환하는 정적 팩토리 메서드
     *
     * @param studyApplicationPage 스터디 참여 신청 목록 페이지
     * @return 스터디 참여 제안 목록 응답 DTO
     */
    public static StudyApplicationsResponseDto of(Page<StudyApplication> studyApplicationPage) {
        List<StudyApplicationDetailsResponseDto> studyApplications = new ArrayList<>();
        for (StudyApplication studyApplication : studyApplicationPage.getContent()) {
            studyApplications.add(StudyApplicationDetailsResponseDto.of(studyApplication));
        }
        return StudyApplicationsResponseDto.builder()
            .totalPages(studyApplicationPage.getTotalPages())
            .isLastPage(studyApplicationPage.isLast())
            .totalStudyApplications(studyApplicationPage.getTotalElements())
            .studyApplications(studyApplications)
            .build();
    }
}
