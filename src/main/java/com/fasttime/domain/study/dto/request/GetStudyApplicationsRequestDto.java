package com.fasttime.domain.study.dto.request;

import lombok.Builder;

/**
 * 스터디 참여 신청 목록 조회 요청 DTO
 *
 * @param applicantId 스터디 참여 신청 회원 식별자
 * @param studyId     스터디 식별자
 */
@Builder
public record GetStudyApplicationsRequestDto(
    long applicantId,
    Long studyId
) {

}
