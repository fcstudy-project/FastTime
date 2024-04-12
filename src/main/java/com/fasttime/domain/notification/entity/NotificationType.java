package com.fasttime.domain.notification.entity;

import lombok.Getter;

/**
 * 알림 종류 enum
 */
@Getter
public enum NotificationType {
    /**
     * 스터디 참여 지원
     */
    STUDY_APPLICATION("새로운 스터디 지원이 들어왔습니다."),

    /**
     * 스터디 참여 제안
     */
    STUDY_SUGGEST("새로운 스터디 참여 제안이 있습니다."),

    /**
     * 스터디 참여 승인
     */
    STUDY_APPROVE("스터디 참여가 승인되었습니다."),

    /**
     * 스터디 참여 거절
     */
    STUDY_REJECT("스터디 참여가 거절되었습니다.");

    /**
     * 알림 종류에 맞는 알림 메시지
     */
    private final String message;

    NotificationType(String message) {
        this.message = message;
    }
}
