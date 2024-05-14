package com.fasttime.domain.study.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.notification.annotation.NeedNotification;
import com.fasttime.domain.study.dto.notification.ApplyToStudyNotificationDto;
import com.fasttime.domain.study.dto.notification.ApproveStudyApplicationNotificationDto;
import com.fasttime.domain.study.dto.notification.RejectStudyApplicationNotificationDto;
import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.dto.response.StudyApplicationsResponseDto;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyApplication;
import com.fasttime.domain.study.entity.StudyRequestStatus;
import com.fasttime.domain.study.exception.NotStudyWriterException;
import com.fasttime.domain.study.exception.StudyApplicationNotFoundException;
import com.fasttime.domain.study.exception.StudyDeleteException;
import com.fasttime.domain.study.exception.StudyNotFoundException;
import com.fasttime.domain.study.repository.StudyApplicationRepository;
import com.fasttime.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스터디 참여 신청 서비스 구현체
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Service
@RequiredArgsConstructor
public class StudyApplicationServiceImpl implements StudyApplicationService {

    private final MemberService memberService;
    private final StudyRepository studyRepository;
    private final StudyApplicationRepository studyApplicationRepository;

    /**
     * 스터디에 참여를 신청하는 메서드
     *
     * @param applicantId            스터디 참여를 신청하는 회원 식별자
     * @param studyId                참여 신청하고자 하는 스터디 식별자
     * @param applyToStudyRequestDto 스터디 참여 신청 요청 DTO
     * @return 스터디 참여 신청 응답 DTO
     */
    @Override
    @Transactional
    public StudyApplicationResponseDto apply(
        long applicantId,
        long studyId,
        ApplyToStudyRequestDto applyToStudyRequestDto
    ) {
        Member applicant = memberService.getMember(applicantId);
        Study study = getStudy(studyId);
        StudyApplication studyApplication = createStudyApplication(
            applicant,
            study,
            applyToStudyRequestDto.message()
        );
        sendStudyApplicationNotification(studyApplication);
        return new StudyApplicationResponseDto(studyApplication.getId());
    }

    /**
     * 스터디 참여 신청 목록 조회 메서드
     *
     * @param getStudyApplicationsRequestDto 스터디 참여 신청 목록 조회 요청 DTO
     * @param pageRequest                    페이지네이션을 위한 PageRequest 객체
     * @return 스터디 참여 신청 목록 조회 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public StudyApplicationsResponseDto getStudyApplications(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto,
        PageRequest pageRequest
    ) {
        Page<StudyApplication> studyApplications;
        studyApplications = studyApplicationRepository.findAllByConditions(
            getStudyApplicationsRequestDto,
            pageRequest
        );
        return StudyApplicationsResponseDto.of(studyApplications);
    }

    /**
     * 스터디 참여 신청 승인 메서드
     *
     * @param memberId           스터디 참여 신청을 승인하고자 하는 회원 식별자
     * @param studyApplicationId 승인할 스터디 참여 신청 식별자
     * @return 스터디 참여 신청 응답 DTO
     */
    @Override
    @Transactional
    public StudyApplicationResponseDto approve(long memberId, long studyApplicationId) {
        StudyApplication studyApplication = getStudyApplication(studyApplicationId);
        authValidation(memberId, studyApplication.getStudy());
        studyApplication.changeStatus(StudyRequestStatus.APPROVED);
        sendNotificationOfStudyApplicationApproval(studyApplication);
        return new StudyApplicationResponseDto(studyApplication.getId());
    }

    /**
     * 스터디 참여 신청 거절 메서드
     *
     * @param memberId           스터디 참여 신청을 거절하고자 하는 회원 식별자
     * @param studyApplicationId 거절할 스터디 참여 신청 식별자
     * @return 스터디 참여 신청 응답 DTO
     */
    @Override
    @Transactional
    public StudyApplicationResponseDto reject(long memberId, long studyApplicationId) {
        StudyApplication studyApplication = getStudyApplication(studyApplicationId);
        authValidation(memberId, studyApplication.getStudy());
        studyApplication.changeStatus(StudyRequestStatus.REJECT);
        sendNotificationOfRejectionOfStudyApplication(studyApplication);
        return new StudyApplicationResponseDto(studyApplication.getId());
    }

    /**
     * 스터디 참여 신청 알림 전송 메서드
     *
     * @param studyApplication 스터디 참여 신청 Entity
     * @return 스터디 참여 신청 알림 DTO
     */
    @NeedNotification
    private ApplyToStudyNotificationDto sendStudyApplicationNotification(
        StudyApplication studyApplication
    ) {
        return new ApplyToStudyNotificationDto(studyApplication);
    }

    /**
     * 스터디 참여 신청 승인 알림 전송 메서드
     *
     * @param studyApplication 스터디 참여 신청 Entity
     * @return 스터디 참여 신청 승인 알림 DTO
     */
    @NeedNotification
    private ApproveStudyApplicationNotificationDto sendNotificationOfStudyApplicationApproval(
        StudyApplication studyApplication
    ) {
        return new ApproveStudyApplicationNotificationDto(studyApplication);
    }

    /**
     * 스터디 참여 신청 거절 알림 전송 메서드
     *
     * @param studyApplication 스터디 참여 신청 Entity
     * @return 스터디 참여 신청 거절 알림 DTO
     */
    @NeedNotification
    private RejectStudyApplicationNotificationDto sendNotificationOfRejectionOfStudyApplication(
        StudyApplication studyApplication
    ) {
        return new RejectStudyApplicationNotificationDto(studyApplication);
    }

    /**
     * 스터디 참여 신청 Entity 생성 메서드
     *
     * @param applicant 스터디 참여를 신청하는 회원 Entity
     * @param study     참여 신청할 스터디 Entity
     * @param message   스터디 참여 신청 메시지
     * @return 생성한 스터디 참여 신청 Entity
     */
    private StudyApplication createStudyApplication(
        Member applicant,
        Study study,
        String message
    ) {
        return studyApplicationRepository.save(
            StudyApplication.builder()
                .status(StudyRequestStatus.CONSIDERING)
                .applicant(applicant)
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
     * 스터디 참여 신청 Entity 조회 메서드
     *
     * @param studyApplicationId 스터디 참여 신청 식별자
     * @return 스터디 참여 신청 Entity
     */
    private StudyApplication getStudyApplication(long studyApplicationId) {
        return studyApplicationRepository.findById(studyApplicationId)
            .orElseThrow(StudyApplicationNotFoundException::new);
    }

    /**
     * 스터디 참여 신청 승인/거절 권한 유효성 검사 메서드
     * <p>
     * 스터디 모집글 작성자가 맞는지, 스터디 모집글이 삭제되지는 않았는지 확인합니다.
     *
     * @param memberId 검사할 회원 식별자
     * @param study    검사할 스터디 Entity
     */
    private void authValidation(long memberId, Study study) {
        if (!isStudyWriter(memberId, study)) {
            throw new NotStudyWriterException();
        }
        if (study.isDeleted()) {
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
