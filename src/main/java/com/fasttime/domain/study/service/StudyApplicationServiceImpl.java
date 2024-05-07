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

@Service
@RequiredArgsConstructor
public class StudyApplicationServiceImpl implements StudyApplicationService {

    private final MemberService memberService;
    private final StudyRepository studyRepository;
    private final StudyApplicationRepository studyApplicationRepository;

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

    @Override
    @Transactional
    public StudyApplicationResponseDto approve(long memberId, long studyApplicationId) {
        StudyApplication studyApplication = getStudyApplication(studyApplicationId);
        authValidation(memberId, studyApplication.getStudy());
        studyApplication.changeStatus(StudyRequestStatus.APPROVE);
        sendNotificationOfStudyApplicationApproval(studyApplication);
        return new StudyApplicationResponseDto(studyApplication.getId());
    }

    @Override
    @Transactional
    public StudyApplicationResponseDto reject(long memberId, long studyApplicationId) {
        StudyApplication studyApplication = getStudyApplication(studyApplicationId);
        authValidation(memberId, studyApplication.getStudy());
        studyApplication.changeStatus(StudyRequestStatus.REJECT);
        sendNotificationOfRejectionOfStudyApplication(studyApplication);
        return new StudyApplicationResponseDto(studyApplication.getId());
    }

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

    @NeedNotification
    private ApplyToStudyNotificationDto sendStudyApplicationNotification(
        StudyApplication studyApplication
    ) {
        return new ApplyToStudyNotificationDto(studyApplication);
    }

    private Study getStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (study.isDeleted()) {
            throw new StudyDeleteException();
        }
        return study;
    }

    private void authValidation(long memberId, Study study) {
        if (!isStudyWriter(memberId, study)) {
            throw new NotStudyWriterException();
        }
        if (study.isDeleted()) {
            throw new StudyDeleteException();
        }
    }

    private boolean isStudyWriter(long memberId, Study study) {
        return study.getMember().getId().equals(memberId);
    }

    private StudyApplication getStudyApplication(long studyApplicationId) {
        return studyApplicationRepository.findById(studyApplicationId)
            .orElseThrow(StudyApplicationNotFoundException::new);
    }

    @NeedNotification
    private ApproveStudyApplicationNotificationDto sendNotificationOfStudyApplicationApproval(
        StudyApplication studyApplication
    ) {
        return new ApproveStudyApplicationNotificationDto(studyApplication);
    }

    @NeedNotification
    private RejectStudyApplicationNotificationDto sendNotificationOfRejectionOfStudyApplication(
        StudyApplication studyApplication
    ) {
        return new RejectStudyApplicationNotificationDto(studyApplication);
    }
}
