package com.fasttime.domain.notification.aop;

import com.fasttime.domain.notification.aop.proxy.NotificationInfo;
import com.fasttime.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotificationAspect {

    private final NotificationService notificationService;

    /**
     * 부가 기능을 적용할 위치 Pointcut 정의
     */
    @Pointcut("@annotation(com.fasttime.domain.notification.annotation.NeedNotification)")
    public void annotationPointcut() {

    }

    /**
     * 알림 전송 Advice 정의 메서드
     *
     * @param joinPoint 조인 포인트
     * @param result    메서드 반환 객체
     */
    @Async
    @AfterReturning(pointcut = "annotationPointcut()", returning = "result")
    public void sendNotification(JoinPoint joinPoint, Object result) {
        NotificationInfo notificationInfo = (NotificationInfo) result;
        notificationService.send(
            notificationInfo.getReceiver(),
            notificationInfo.getNotificationType(),
            notificationInfo.getNotificationType().getMessage(),
            notificationInfo.getUrl()
        );
    }
}
