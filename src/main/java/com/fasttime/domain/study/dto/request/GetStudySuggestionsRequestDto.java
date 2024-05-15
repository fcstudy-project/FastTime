package com.fasttime.domain.study.dto.request;

import lombok.Builder;

/**
 * 스터디 참여 제안 요청 목록 조회 요청 DTO
 *
 * @param receiverId 스터디 참여 제안을 받은 회원 식별자
 * @param studyId    스터디 식별자
 */
@Builder
public record GetStudySuggestionsRequestDto(
    long receiverId,
    Long studyId
) {

}
