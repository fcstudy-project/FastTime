package com.fasttime.domain.notification.repository;

import com.fasttime.domain.notification.entity.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class EmitterRepositoryImpl implements EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Notification> eventCaches = new ConcurrentHashMap<>();

    /**
     * Emitter를 저장하는 메서드
     *
     * @param emitterId  저장할 Emitter 식별자
     * @param sseEmitter 저장할 SseEmitter
     * @return 저장한 SseEmitter
     */
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    /**
     * 이벤트를 저장하는 메서드
     *
     * @param eventCacheId 저장할 이벤트 캐시 식별자
     * @param event        저장할 이벤트
     */
    @Override
    public void saveEventCache(String eventCacheId, Notification event) {
        eventCaches.put(eventCacheId, event);
    }

    /**
     * 해당 회원과 관련된 모든 Emitter를 찾는 메서드
     *
     * @param memberId Emitter를 찾을 회원 식별자
     * @return Emitter 맵
     */
    @Override
    public Map<String, SseEmitter> findAllByMemberId(long memberId) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(String.valueOf(memberId)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 해당 회원과 관련된 모든 이벤트 캐시를 찾는 메서드
     *
     * @param memberId 이벤트 캐시를 찾을 회원 식별자
     * @return 이벤트 캐시 맵
     */
    @Override
    public Map<String, Object> findAllEventCacheByMemberId(long memberId) {
        return eventCaches.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(String.valueOf(memberId)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Emitter를 지우는 메서드
     *
     * @param id 지울 Emitter 식별자
     */
    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }

    /**
     * 해당 회원과 관련된 모든 Emitter를 지우는 메서드
     *
     * @param memberId 관련된 Emitter를 지울 회원 식별자
     */
    @Override
    public void deleteAllByMemberId(long memberId) {
        emitters.forEach((key, emitter) -> {
            if (key.startsWith(String.valueOf(memberId))) {
                emitters.remove(key);
            }
        });
    }

    /**
     * 해당 회원과 관련된 모든 이벤트 캐시를 지우는 메서드
     *
     * @param memberId 관련된 이벤트 캐시를 지울 회원 식별자
     */
    @Override
    public void deleteAllEventCacheByMemberId(long memberId) {
        eventCaches.forEach((key, emitter) -> {
            if (key.startsWith(String.valueOf(memberId))) {
                eventCaches.remove(key);
            }
        });
    }
}
