package com.fasttime.domain.study.dto.notification;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.aop.proxy.NotificationInfo;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.study.entity.StudySuggestion;

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
