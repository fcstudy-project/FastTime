package com.fasttime.domain.notification.aop.proxy;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.entity.NotificationType;

public interface NotificationInfo {

    /**
     * 알림을 수신할 회원 Entity Getter 메서드
     *
     * @return 알림을 수신할 회원 Entity
     */
    Member getReceiver();

    /**
     * 알림과 관련된 URL 문자열 Getter 메서드
     *
     * @return 알림과 관련된 URL 문자열
     */
    String getUrl();

    /**
     * 알림 종류 Getter 메서드
     *
     * @return 알림 종류 Enum
     */
    NotificationType getNotificationType();
}
