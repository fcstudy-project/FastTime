package com.fasttime.domain.notification.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.entity.NotificationType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    public SseEmitter subscribe(Long memberId, String lastEventId);

    public void send(
        Member receiver,
        NotificationType notificationType,
        String content,
        String url
    );
}
