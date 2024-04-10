package com.fasttime.domain.notification.aop.proxy;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.entity.NotificationType;

public interface NotificationInfo {

    Member getReceiver();

    String getUrl();

    NotificationType getNotificationType();
}
