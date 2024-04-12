package com.fasttime.domain.notification.controller;

import com.fasttime.domain.notification.service.NotificationService;
import com.fasttime.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    /**
     * 알림 구독 API
     * <p>
     * 알림 구독 요청을 받아, SSE(Server-Send Event) 방식으로 알림을 제공하도록 합니다. Last-Event-ID 라는 헤더를 통해 선택적으로 마지막
     * 이벤트 식별자를 받고, 그 이후의 데이터부터 받을 수 있도록 합니다. 이 때, SSE 연결 시간 만료 혹은 종료로 인해 받지 못한 이벤트가 있다면 해당 데이터들도 받을
     * 수 있습니다.
     *
     * @param lastEventId Last-Event-ID 라는 헤더를 통해 받은 마지막 이벤트 식별자 (필수 아님)
     * @return SseEmitter 객체 (유효 시간: 1시간)
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        long memberId = securityUtil.getCurrentMemberId();
        return notificationService.subscribe(memberId, lastEventId);
    }
}
