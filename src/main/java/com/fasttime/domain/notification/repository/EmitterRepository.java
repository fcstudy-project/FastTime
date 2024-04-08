package com.fasttime.domain.notification.repository;

import com.fasttime.domain.notification.entity.Notification;
import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String eventCacheId, Notification event);

    Map<String, SseEmitter> findAllByMemberId(long memberId);

    Map<String, Object> findAllEventCacheByMemberId(long memberId);

    void deleteById(String id);

    void deleteAllByMemberId(long memberId);

    void deleteAllEventCacheByMemberId(long memberId);
}
