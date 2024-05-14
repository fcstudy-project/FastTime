package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudyApplication;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

/**
 * 스터디 참여 신청 상세 응답 DTO
 *
 * @param id          스터디 참여 신청 식별자
 * @param status      스터디 참여 신청 상태
 * @param studyId     참여 신청하고자 한 스터디 식별자
 * @param applicantId 참여 신청한 회원 식별자
 * @param nickname    참여 신청한 회원 닉네임
 * @param message     참여 신청 시 보낸 메시지
 * @param createdAt   스터디 참여 신청 생성일
 * @param updatedAt   스터디 참여 신청 수정일
 * @param deletedAt   스터디 참여 신청 삭제일
 */
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
    String deletedAt
) {

    /**
     * 스터디 참여 신청 Entity를 스터디 참여 신청 응답 DTO로 변환하는 정적 팩토리 메서드
     *
     * @param studyApplication 스터디 참여 신청 Entity
     * @return 스터디 참여 신청 응답 DTO
     */
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
            .deletedAt(dateTimeParse(studyApplication.getDeletedAt()))
            .build();
    }

    private static String dateTimeParse(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
