package com.fasttime.domain.notification.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.notification.dto.response.NotificationResponseDto;
import com.fasttime.domain.notification.entity.Notification;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.notification.exception.SseConnectionFailedException;
import com.fasttime.domain.notification.repository.EmitterRepository;
import com.fasttime.domain.notification.repository.NotificationRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    /**
     * SseEmitter를 생성하여 저장시켜두는 구독 메서드
     *
     * @param memberId    구독자 회원 식별자
     * @param lastEventId 마지막
     * @return 생성한 SseEmitter
     */
    @Override
    public SseEmitter subscribe(Long memberId, String lastEventId) {
        // Emitter 생성 & 저장
        String emitterId = makeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // 상황 별 emitter 삭제 처리
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        // 503 에러 방지용 더미 이벤트 전송
        String eventId = makeId(memberId);
        sendNotification(emitter, eventId, emitterId,
            "Event stream created. [memberId=" + memberId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재하는 경우
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, memberId, emitterId, emitter);
        }

        return emitter;
    }

    /**
     * 어떤 회원이 사용하는 SseEmitter인지 구분해줄 식별자를 생성하는 메서드
     *
     * @param memberId 구독자 회원 식별자
     * @return Emitter 식별자
     */
    private String makeId(long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    /**
     * 실질적으로 알림을 전송하는 메서드
     *
     * @param emitter   전송할 emitter
     * @param eventId   전송할 이벤트 식별자
     * @param emitterId 전송할 emitter 식별자
     * @param data      전송할 데이터
     */
    private void sendNotification(
        SseEmitter emitter,
        String eventId,
        String emitterId,
        Object data
    ) {
        try {
            emitter.send(SseEmitter.event().id(eventId).data(data, MediaType.APPLICATION_JSON));
            emitterRepository.deleteById(emitterId);
        } catch (IOException e) {
            emitter.completeWithError(e);
            throw new SseConnectionFailedException();
        }
    }

    /**
     * 미수신된 데이터의 유무를 반환하는 메서드
     *
     * @param lastEventId 마지막 이벤트 식별자
     * @return 미수신 데이터 유무
     */
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    /**
     * 미수신한 데이터 전송하는 메서드
     *
     * @param lastEventId 마지막 이벤트 식별자
     * @param memberId    수신자 식별자
     * @param emitterId   Emitter 식별자
     * @param emitter     Emitter
     */
    private void sendLostData(String lastEventId, long memberId, String emitterId,
        SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheByMemberId(memberId);
        eventCaches.entrySet().stream()
            .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
            .forEach(entry ->
                sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    /**
     * 특정 회원에게 알림을 전송하는 메서드
     *
     * @param receiver         수신자 회원 Entity
     * @param notificationType 알림 종류
     * @param content          알림 내용
     * @param url              알림과 연결된 URL
     */
    @Override
    public void send(
        Member receiver,
        NotificationType notificationType,
        String content,
        String url
    ) {
        // 알림 Entity 생성 및 저장
        Notification notification = notificationRepository.save(
            Notification.builder()
                .type(notificationType)
                .content(content)
                .url(url)
                .isRead(false)
                .receiver(receiver)
                .build()
        );

        // 알림 수신자의 Emitter 를 모두 찾아 전송
        String eventId = makeId(receiver.getId());
        Map<String, SseEmitter> emitters = emitterRepository.findAllByMemberId(receiver.getId());
        emitters.forEach((emitterId, emitter) -> {
            emitterRepository.saveEventCache(emitterId, notification);
            sendNotification(emitter, eventId, emitterId, NotificationResponseDto.of(notification));
        });
    }
}
