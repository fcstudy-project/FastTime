package com.fasttime.domain.study.dto.notification;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.aop.proxy.NotificationInfo;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.study.entity.StudyApplication;

public record ApproveStudyApplicationNotificationDto(
    StudyApplication studyApplication
) implements NotificationInfo {

    @Override
    public Member getReceiver() {
        return studyApplication.getApplicant();
    }

    @Override
    public String getUrl() {
        return "/api/v2/studies/" + studyApplication.getStudy().getId();
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.STUDY_APPROVE_APPLICATION;
    }
}
