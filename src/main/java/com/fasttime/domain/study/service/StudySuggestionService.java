package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionsResponseDto;
import org.springframework.data.domain.PageRequest;

/**
 * 스터디 참여 제안 서비스 인터페이스
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface StudySuggestionService {

    /**
     * 스터디 참여 제안 메서드
     *
     * @param memberId               스터디 참여를 제안하는 회원(스터디 모집글 작성자) 식별자
     * @param receiverId             스터디 참여 제안을 받을 회원 식별자
     * @param studyId                참여를 제안할 스터디 식별자
     * @param suggestStudyRequestDto 스터디 참여 제안 요청 DTO
     * @return 스터디 참여 제안 응답 DTO
     */
    StudySuggestionResponseDto suggest(
        long memberId,
        long receiverId,
        long studyId,
        SuggestStudyRequestDto suggestStudyRequestDto
    );

    /**
     * 스터디 참여 제안 목록 조회 메서드
     *
     * @param getStudySuggestionsRequestDto 스터디 참여 제안 목록 조회 요청 DTO
     * @param pageRequest                   페이지네이션을 위한 PageRequest 객체
     * @return 스터디 참여 제안 목록 조회 응답 DTO
     */
    StudySuggestionsResponseDto getStudySuggestions(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto,
        PageRequest pageRequest
    );

    /**
     * 스터디 참여 제안 승인 메서드
     *
     * @param memberId          스터디 참여 제안을 승인하고자 하는 회원 식별자
     * @param studySuggestionId 스터디 참여 제안 식별자
     * @return 스터디 참여 제안 응답 DTO
     */
    StudySuggestionResponseDto approve(
        long memberId,
        long studySuggestionId
    );

    /**
     * 스터디 참여 제안 거절 메서드
     *
     * @param memberId          스터디 참여 제안을 거절하고자 하는 회원 식별자
     * @param studySuggestionId 스터디 참여 제안 식별자
     * @return 스터디 참여 제안 응답 DTO
     */
    StudySuggestionResponseDto reject(
        long memberId,
        long studySuggestionId
    );
}
