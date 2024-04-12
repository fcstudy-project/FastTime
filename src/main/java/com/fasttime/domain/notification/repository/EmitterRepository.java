package com.fasttime.domain.notification.repository;

import com.fasttime.domain.notification.entity.Notification;
import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    /**
     * Emitter를 저장하는 메서드
     *
     * @param emitterId  저장할 Emitter 식별자
     * @param sseEmitter 저장할 SseEmitter
     * @return 저장한 SseEmitter
     */
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    /**
     * 이벤트를 저장하는 메서드
     *
     * @param eventCacheId 저장할 이벤트 캐시 식별자
     * @param event        저장할 이벤트
     */
    void saveEventCache(String eventCacheId, Notification event);

    /**
     * 해당 회원과 관련된 모든 Emitter를 찾는 메서드
     *
     * @param memberId Emitter를 찾을 회원 식별자
     * @return Emitter 맵
     */
    Map<String, SseEmitter> findAllByMemberId(long memberId);

    /**
     * 해당 회원과 관련된 모든 이벤트 캐시를 찾는 메서드
     *
     * @param memberId 이벤트 캐시를 찾을 회원 식별자
     * @return 이벤트 캐시 맵
     */
    Map<String, Object> findAllEventCacheByMemberId(long memberId);

    /**
     * Emitter를 지우는 메서드
     *
     * @param id 지울 Emitter 식별자
     */
    void deleteById(String id);

    /**
     * 해당 회원과 관련된 모든 Emitter를 지우는 메서드
     *
     * @param memberId 관련된 Emitter를 지울 회원 식별자
     */
    void deleteAllByMemberId(long memberId);

    /**
     * 해당 회원과 관련된 모든 이벤트 캐시를 지우는 메서드
     *
     * @param memberId 관련된 이벤트 캐시를 지울 회원 식별자
     */
    void deleteAllEventCacheByMemberId(long memberId);
}
