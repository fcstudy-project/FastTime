package com.fasttime.domain.study.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.service.MemberService;
import com.fasttime.domain.notification.annotation.NeedNotification;
import com.fasttime.domain.study.dto.notification.ApproveStudySuggestionNotificationDto;
import com.fasttime.domain.study.dto.notification.RejectStudySuggestionNotificationDto;
import com.fasttime.domain.study.dto.notification.SuggestStudyNotificationDto;
import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudySuggestionServiceImpl implements StudySuggestionService {

    private final MemberService memberService;
    private final StudyRepository studyRepository;
    private final StudySuggestionRepository studySuggestionRepository;

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

    @Override
    @Transactional
    public StudySuggestionResponseDto approve(long memberId, long studySuggestionId) {
        StudySuggestion studySuggestion = getStudySuggestion(studySuggestionId);
        authToApproveOrRejectSuggestionValidation(memberId, studySuggestion);
        studySuggestion.changeStatus(StudyRequestStatus.APPROVE);
        sendNotificationOfStudySuggestionApproval(studySuggestion);
        return new StudySuggestionResponseDto(studySuggestion.getId());
    }

    @Override
    @Transactional
    public StudySuggestionResponseDto reject(long memberId, long studySuggestionId) {
        StudySuggestion studySuggestion = getStudySuggestion(studySuggestionId);
        authToApproveOrRejectSuggestionValidation(memberId, studySuggestion);
        studySuggestion.changeStatus(StudyRequestStatus.REJECT);
        sendNotificationOfRejectionOfStudySuggestion(studySuggestion);
        return new StudySuggestionResponseDto(studySuggestion.getId());
    }

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

    @NeedNotification
    private SuggestStudyNotificationDto sendStudySuggestionNotification(
        Member receiver,
        StudySuggestion studySuggestion
    ) {
        return new SuggestStudyNotificationDto(receiver, studySuggestion);
    }

    private void authToMakeSuggestionValidation(long memberId, Study study) {
        if (!isStudyWriter(memberId, study)) {
            throw new NotStudyWriterException();
        }
        if (study.isDeleted()) {
            throw new StudyDeleteException();
        }
    }

    private void authToApproveOrRejectSuggestionValidation(long memberId,
        StudySuggestion studySuggestion) {
        if (studySuggestion.getReceiver().getId().equals(memberId)) {
            throw new NotStudySuggestionReceiverException();
        }
        if (studySuggestion.getStudy().isDeleted()) {
            throw new StudyDeleteException();
        }
    }

    private boolean isStudyWriter(long memberId, Study study) {
        return study.getMember().getId().equals(memberId);
    }

    private Study getStudy(Long studyId) {
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (study.isDeleted()) {
            throw new StudyDeleteException();
        }
        return study;
    }

    private StudySuggestion getStudySuggestion(long studySuggestionId) {
        return studySuggestionRepository.findById(studySuggestionId)
            .orElseThrow(StudySuggestionNotFoundException::new);
    }

    @NeedNotification
    private ApproveStudySuggestionNotificationDto sendNotificationOfStudySuggestionApproval(
        StudySuggestion studySuggestion
    ) {
        return new ApproveStudySuggestionNotificationDto(studySuggestion);
    }

    @NeedNotification
    private RejectStudySuggestionNotificationDto sendNotificationOfRejectionOfStudySuggestion(
        StudySuggestion studySuggestion
    ) {
        return new RejectStudySuggestionNotificationDto(studySuggestion);
    }
}
