package com.fasttime.domain.study.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.notification.annotation.NeedNotification;
import com.fasttime.domain.study.dto.notification.ApproveStudySuggestionNotificationDto;
import com.fasttime.domain.study.dto.notification.RejectStudySuggestionNotificationDto;
import com.fasttime.domain.study.dto.notification.SuggestStudyNotificationDto;
import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionsResponseDto;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyRequestStatus;
import com.fasttime.domain.study.entity.StudySuggestion;
import com.fasttime.domain.study.exception.NotStudySuggestionReceiverException;
import com.fasttime.domain.study.exception.NotStudyWriterException;
import com.fasttime.domain.study.exception.StudyDeleteException;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.exception.StudySuggestionNotFoundException;
import com.fasttime.domain.study.repository.StudyRepository;
import com.fasttime.domain.study.repository.StudySuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스터디 참여 제안 서비스 구현체
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class StudySuggestionServiceImpl implements StudySuggestionService {

    private final MemberService memberService;
    private final StudyRepository studyRepository;
    private final StudySuggestionRepository studySuggestionRepository;

    /**
     * 스터디 참여 제안 메서드
     *
     * @param memberId               스터디 참여를 제안하는 회원(스터디 모집글 작성자) 식별자
     * @param receiverId             스터디 참여 제안을 받을 회원 식별자
     * @param studyId                참여를 제안할 스터디 식별자
     * @param suggestStudyRequestDto 스터디 참여 제안 요청 DTO
     * @return 스터디 참여 제안 응답 DTO
     */
    @Override
    @Transactional
    public StudySuggestionResponseDto suggest(
        long memberId,
        long receiverId,
        long studyId,
        SuggestStudyRequestDto suggestStudyRequestDto
    ) {
        Member receiver = memberService.getMember(receiverId);
        Study study = getStudy(studyId);
        authToMakeSuggestionValidation(memberId, study);
        StudySuggestion studySuggestion = createStudySuggestion(
            receiver,
            study,
            suggestStudyRequestDto.message()
        );
        sendStudySuggestionNotification(receiver, studySuggestion);
        return new StudySuggestionResponseDto(studySuggestion.getId());
    }

    /**
     * 스터디 참여 제안 목록 조회 메서드
     *
     * @param getStudySuggestionsRequestDto 스터디 참여 제안 목록 조회 요청 DTO
     * @param pageRequest                   페이지네이션을 위한 PageRequest 객체
     * @return 스터디 참여 제안 목록 조회 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public StudySuggestionsResponseDto getStudySuggestions(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto, PageRequest pageRequest) {
        Page<StudySuggestion> studySuggestions;
        studySuggestions = studySuggestionRepository.findAllByConditions(
            getStudySuggestionsRequestDto,
            pageRequest
        );
        return StudySuggestionsResponseDto.of(studySuggestions);
    }

    /**
     * 스터디 참여 제안 승인 메서드
     *
     * @param memberId          스터디 참여 제안을 승인하고자 하는 회원 식별자
     * @param studySuggestionId 스터디 참여 제안 식별자
     * @return 스터디 참여 제안 응답 DTO
     */
    @Override
    @Transactional
    public StudySuggestionResponseDto approve(long memberId, long studySuggestionId) {
        StudySuggestion studySuggestion = getStudySuggestion(studySuggestionId);
        authToApproveOrRejectSuggestionValidation(memberId, studySuggestion);
        studySuggestion.changeStatus(StudyRequestStatus.APPROVED);
        sendNotificationOfStudySuggestionApproval(studySuggestion);
        return new StudySuggestionResponseDto(studySuggestion.getId());
    }

    /**
     * 스터디 참여 제안 거절 메서드
     *
     * @param memberId          스터디 참여 제안을 거절하고자 하는 회원 식별자
     * @param studySuggestionId 스터디 참여 제안 식별자
     * @return 스터디 참여 제안 응답 DTO
     */
    @Override
    @Transactional
    public StudySuggestionResponseDto reject(long memberId, long studySuggestionId) {
        StudySuggestion studySuggestion = getStudySuggestion(studySuggestionId);
        authToApproveOrRejectSuggestionValidation(memberId, studySuggestion);
        studySuggestion.changeStatus(StudyRequestStatus.REJECT);
        sendNotificationOfRejectionOfStudySuggestion(studySuggestion);
        return new StudySuggestionResponseDto(studySuggestion.getId());
    }

    /**
     * 스터디 참여 제안 알림 전송 메서드
     *
     * @param receiver        스터디 참여 제안을 받은 회원 Entity
     * @param studySuggestion 스터디 참여 제안 Entity
     * @return 스터디 참여 제안 알림 DTO
     */
    @NeedNotification
    private SuggestStudyNotificationDto sendStudySuggestionNotification(
        Member receiver,
        StudySuggestion studySuggestion
    ) {
        return new SuggestStudyNotificationDto(receiver, studySuggestion);
    }

    /**
     * 스터디 참여 제안 승인 알림 전송 메서드
     *
     * @param studySuggestion 스터디 참여 제안 Entity
     * @return 스터디 참여 제안 승인 알림 DTO
     */
    @NeedNotification
    private ApproveStudySuggestionNotificationDto sendNotificationOfStudySuggestionApproval(
        StudySuggestion studySuggestion
    ) {
        return new ApproveStudySuggestionNotificationDto(studySuggestion);
    }

    /**
     * 스터디 참여 제안 거절 알림 전송 메서드
     *
     * @param studySuggestion 스터디 참여 제안 Entity
     * @return 스터디 참여 제안 거절 알림 DTO
     */
    @NeedNotification
    private RejectStudySuggestionNotificationDto sendNotificationOfRejectionOfStudySuggestion(
        StudySuggestion studySuggestion
    ) {
        return new RejectStudySuggestionNotificationDto(studySuggestion);
    }

    /**
     * 스터디 참여 제안 Entity 생성 메서드
     *
     * @param receiver 스터디 참여 제안을 받을 회원 Entity
     * @param study    참여 제안할 스터디 Entity
     * @param message  스터디 참여 제안 메시지
     * @return 스터디 참여 제안 Entity
     */
    private StudySuggestion createStudySuggestion(
        Member receiver,
        Study study,
        String message
    ) {
        return studySuggestionRepository.save(
            StudySuggestion.builder()
                .status(StudyRequestStatus.CONSIDERING)
                .receiver(receiver)
                .study(study)
                .message(message)
                .build()
        );
    }

    /**
     * 스터디 Entity 조회 메서드
     *
     * @param studyId 조회할 스터디 식별자
     * @return 스터디 Entity
     */
    private Study getStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (study.isDeleted()) {
            throw new StudyDeleteException();
        }
        return study;
    }

    /**
     * 스터디 참여 제안 Entity 조회 메서드
     *
     * @param studySuggestionId 스터디 참여 제안 식별자
     * @return 스터디 참여 제안 Entity
     */
    private StudySuggestion getStudySuggestion(long studySuggestionId) {
        return studySuggestionRepository.findById(studySuggestionId)
            .orElseThrow(StudySuggestionNotFoundException::new);
    }

    /**
     * 스터디 참여 제안 유효성 검사 메서드
     * <p>
     * 스터디 모집글 작성자가 맞는지, 스터디 모집글이 삭제되지는 않았는지 확인합니다.
     *
     * @param memberId 검사할 회원 식별자
     * @param study    검사할 스터디 Entity
     */
    private void authToMakeSuggestionValidation(long memberId, Study study) {
        if (!isStudyWriter(memberId, study)) {
            throw new NotStudyWriterException();
        }
        if (study.isDeleted()) {
            throw new StudyDeleteException();
        }
    }

    /**
     * 스터디 참여 제안 승인/거절 유효성 검사 메서드
     * <p>
     * 스터디 참여 제안 수신자가 맞는지, 스터디 모집글이 삭제되지는 않았는지 확인합니다.
     *
     * @param memberId        검사할 회원 식별자
     * @param studySuggestion 검사할 스터디 Entity
     */
    private void authToApproveOrRejectSuggestionValidation(long memberId,
        StudySuggestion studySuggestion) {
        if (studySuggestion.getReceiver().getId().equals(memberId)) {
            throw new NotStudySuggestionReceiverException();
        }
        if (studySuggestion.getStudy().isDeleted()) {
            throw new StudyDeleteException();
        }
    }

    /**
     * 스터디 모집글 작성자가 맞는지 확인하는 메서드
     *
     * @param memberId 회원 식별자
     * @param study    스터디 Entity
     * @return 요청 회원과 스터디 모집글 작성자 일치 여부
     */
    private boolean isStudyWriter(long memberId, Study study) {
        return study.getMember().getId().equals(memberId);
    }
}
