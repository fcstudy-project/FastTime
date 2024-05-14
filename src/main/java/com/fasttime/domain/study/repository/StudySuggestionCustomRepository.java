package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.entity.StudySuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 스터디 참여 제안 커스텀 레포지토리
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface StudySuggestionCustomRepository {

    /**
     * 스터디 참여 제안을 검색 조건에 따라 목록 조회하는 메서드
     *
     * @param getStudySuggestionsRequestDto 스터디 참여 제안 조회 요청 DTO
     * @param pageable                       페이지네이션을 위한 pageable 객체
     * @return 스터디 참여 제안 목록 페이지
     */
    Page<StudySuggestion> findAllByConditions(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto,
        Pageable pageable
    );
}
