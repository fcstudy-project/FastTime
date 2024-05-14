package com.fasttime.domain.study.dto.response;

import com.fasttime.domain.study.entity.StudySuggestion;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

/**
 * 스터디 참여 제안 상세 응답 DTO
 *
 * @param id         스터디 참여 제안 식별자
 * @param status     스터디 참여 제안 상태
 * @param studyId    참여 제안하고자 한 스터디 식별자
 * @param receiverId 참여 제안을 받은 회원 식별자
 * @param nickname   참여 제안을 받은  회원 닉네임
 * @param message    참여 신청 시 보낸 메시지
 * @param createdAt  스터디 참여 제안 생성일
 * @param updatedAt  스터디 참여 제안 수정일
 * @param deletedAt  스터디 참여 제안 삭제일
 */
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

    /**
     * 스터디 참여 제안 Entity를 스터디 참여 제안 응답 DTO로 변환하는 정적 팩토리 메서드
     *
     * @param studySuggestion 스터디 참여 제안 Entity
     * @return 스터디 참여 제안 응답 DTO
     */
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
