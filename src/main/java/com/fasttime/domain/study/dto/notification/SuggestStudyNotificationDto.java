package com.fasttime.domain.study.dto.notification;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.aop.proxy.NotificationInfo;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.study.entity.StudySuggestion;

/**
 * 스터디 참여 제안 알림 DTO
 * <p>
 * 스터디 모집글 작성자가 스터디 참여 제안 시 제안을 받은 회원에게 알림을 보내기 위한 DTO 입니다.
 *
 * @param receiver        스터디 참여 제안을 받는 회원 Entity
 * @param studySuggestion 스터디 참여 제안 Entity
 */
public record SuggestStudyNotificationDto(
    Member receiver,
    StudySuggestion studySuggestion
) implements NotificationInfo {

    @Override
    public Member getReceiver() {
        return receiver;
    }

    @Override
    public String getUrl() {
        return "/api/v2/studies/" + studySuggestion.getStudy().getId();
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.STUDY_APPLICATION;
    }
}
