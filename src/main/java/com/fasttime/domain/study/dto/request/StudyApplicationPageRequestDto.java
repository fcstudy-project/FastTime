package com.fasttime.domain.study.dto.request;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;

/**
 * 스터디 참여 신청 목록 페이지 요청 DTO
 *
 * @param page 조회하고자 하는 페이지 번호
 * @param size 한 페이지 당 조회할 스터디 참여 신청 개수
 */
public record StudyApplicationPageRequestDto(
    int page,
    int size
) {

    @Builder
    public StudyApplicationPageRequestDto(int page, int size) {
        this.page = Math.max(page, 0);
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public PageRequest of() {
        return PageRequest.of(page, size);
    }
}
