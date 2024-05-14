package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.dto.response.StudyApplicationsResponseDto;
import org.springframework.data.domain.PageRequest;

/**
 * 스터디 참여 신청 서비스 인터페이스
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface StudyApplicationService {

    /**
     * 스터디에 참여를 신청하는 메서드
     *
     * @param applicantId            스터디 참여를 신청하는 회원 식별자
     * @param studyId                참여 신청하고자 하는 스터디 식별자
     * @param applyToStudyRequestDto 스터디 참여 신청 요청 DTO
     * @return 스터디 참여 신청 응답 DTO
     */
    StudyApplicationResponseDto apply(
        long applicantId,
        long studyId,
        ApplyToStudyRequestDto applyToStudyRequestDto
    );

    /**
     * 스터디 참여 신청 목록 조회 메서드
     *
     * @param getStudyApplicationsRequestDto 스터디 참여 신청 목록 조회 요청 DTO
     * @param pageRequest                    페이지네이션을 위한 PageRequest 객체
     * @return 스터디 참여 신청 목록 조회 응답 DTO
     */
    StudyApplicationsResponseDto getStudyApplications(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto,
        PageRequest pageRequest
    );

    /**
     * 스터디 참여 신청 승인 메서드
     *
     * @param memberId           스터디 참여 신청을 승인하고자 하는 회원 식별자
     * @param studyApplicationId 승인할 스터디 참여 신청 식별자
     * @return 스터디 참여 신청 응답 DTO
     */
    StudyApplicationResponseDto approve(
        long memberId,
        long studyApplicationId
    );

    /**
     * 스터디 참여 신청 거절 메서드
     *
     * @param memberId           스터디 참여 신청을 거절하고자 하는 회원 식별자
     * @param studyApplicationId 거절할 스터디 참여 신청 식별자
     * @return 스터디 참여 신청 응답 DTO
     */
    StudyApplicationResponseDto reject(
        long memberId,
        long studyApplicationId
    );
}
