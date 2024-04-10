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

    private static final String STUDY_URL = "/api/v1/studies/";

    @Pointcut("@annotation(com.fasttime.domain.notification.annotation.NeedNotification)")
    public void annotationPointcut() {

    }

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
