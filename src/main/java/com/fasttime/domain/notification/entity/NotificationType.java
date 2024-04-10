package com.fasttime.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    STUDY_APPLICATION("새로운 스터디 지원이 들어왔습니다."),
    STUDY_SUGGEST("새로운 스터디 참여 제안이 있습니다."),
    STUDY_APPROVE("스터디 참여가 승인되었습니다."),
    STUDY_REJECT("스터디 참여가 거절되었습니다.");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }
}
