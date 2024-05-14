package com.fasttime.domain.study.dto.notification;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.aop.proxy.NotificationInfo;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.study.entity.StudyApplication;

/**
 * 스터디 참여 신청 거절 알림 DTO
 * <p>
 * 스터디 모집글 작성자가 스터디 참여 신청을 거절하면 스터디 참여 신청자에게 알림을 보내기 위한 DTO 입니다.
 *
 * @param studyApplication 스터디 참여 신청 Entity
 */
public record RejectStudyApplicationNotificationDto(
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
        return NotificationType.STUDY_REJECT;
    }
}
