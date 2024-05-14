package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.entity.StudyApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 스터디 참여 신청 커스텀 레포지토리
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface StudyApplicationCustomRepository {

    /**
     * 스터디 참여 신청을 검색 조건에 따라 목록 조회하는 메서드
     *
     * @param getStudyApplicationsRequestDto 스터디 참여 신청 조회 요청 DTO
     * @param pageable                       페이지네이션을 위한 pageable 객체
     * @return 스터디 참여 신청 목록 페이지
     */
    Page<StudyApplication> findAllByConditions(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto,
        Pageable pageable
    );
}
