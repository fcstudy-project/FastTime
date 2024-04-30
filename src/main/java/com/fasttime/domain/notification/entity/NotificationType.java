package com.fasttime.domain.notification.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 알림 종류 enum
 * <p>
 * 이 열거형은 다양한 알림 유형을 정의합니다.
 */
@Getter
public enum NotificationType {
    STUDY_APPLICATION("새로운 스터디 지원이 들어왔습니다."),
    STUDY_SUGGEST("새로운 스터디 참여 제안이 있습니다."),
    STUDY_APPROVE_APPLICATION("스터디 참여가 승인되었습니다."),
    STUDY_APPROVE_SUGGESTION("스터디 제안을 승인했습니다."),
    STUDY_REJECT("스터디 참여가 거절되었습니다.");

    /**
     * 알림 종류에 맞는 알림 메시지
     */
    @NotNull
    private final String message;

    /**
     * 주어진 메시지로 알림 종류를 생성하는 생성자
     *
     * @param message 알림 메시지
     */
    NotificationType(@NotNull String message) {
        this.message = message;
    }
}
