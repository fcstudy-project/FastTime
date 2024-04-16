package com.fasttime.domain.notification.unit.repository;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.notification.entity.Notification;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.notification.repository.EmitterRepository;
import com.fasttime.domain.notification.repository.EmitterRepositoryImpl;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class EmitterRepositoryImplTest {

    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    @Test
    @DisplayName("새로운 Emitter를 추가할 수 있다.")
    public void saveWillSuccess() {
        // given
        long memberId = 1L;
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        // when, then
        Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
    }

    @Test
    @DisplayName("수신한 이벤트를 캐시에 저장할 수 있다.")
    public void saveEventCacheWillSuccess() {
        // given
        long memberId = 1L;
        Member member = Member.builder()
            .id(memberId)
            .email("test@mail.com")
            .password("$10$PeUPJ/oN5Kzl..lt1hkB4OVe6L.ESrFa1LHMvBI64TsrYNJU3RXDO")
            .nickname("nickname1")
            .campCrtfc(false)
            .role(Role.ROLE_USER)
            .build();
        String eventCacheId = memberId + "_" + System.currentTimeMillis();
        Notification notification = Notification.builder()
            .type(NotificationType.STUDY_APPLICATION)
            .content("nickname2님이 스터디에 지원하셨습니다.")
            .url("www.boocam.net/studies/1")
            .isRead(false)
            .receiver(member)
            .build();

        // when then
        Assertions.assertDoesNotThrow(
            () -> emitterRepository.saveEventCache(eventCacheId, notification));
    }

    @Test
    @DisplayName("특정 회원이 접속한 모든 Emitter를 찾을 수 있다.")
    public void findAllStartWithByMemberIdWillSuccess() throws InterruptedException {
        // given
        long memberId = 1L;
        String emitterId1 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId3 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));

        // when
        Map<String, SseEmitter> result = emitterRepository.findAllByMemberId(memberId);

        // then
        Assertions.assertEquals(result.size(), 3);
    }

    @Test
    @DisplayName("특정 회원에게 수신된 이벤트를 캐시에서 모두 찾을 수 있다.")
    public void findAllEventCacheStartWithByMemberIdWillSuccess() throws InterruptedException {
        // given
        long memberId = 1L;
        Member member = Member.builder()
            .id(memberId)
            .email("test@mail.com")
            .password("$10$PeUPJ/oN5Kzl..lt1hkB4OVe6L.ESrFa1LHMvBI64TsrYNJU3RXDO")
            .nickname("nickname1")
            .campCrtfc(false)
            .role(Role.ROLE_USER)
            .build();

        String eventCacheId1 = memberId + "_" + System.currentTimeMillis();
        Notification notification1 = Notification.builder()
            .type(NotificationType.STUDY_APPLICATION)
            .content("nickname2님이 스터디에 지원하셨습니다.")
            .url("www.boocam.net/studies/1")
            .isRead(false)
            .receiver(member)
            .build();
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 = memberId + "_" + System.currentTimeMillis();
        Notification notification2 = Notification.builder()
            .type(NotificationType.STUDY_SUGGEST)
            .content("nickname3님이 스터디 참여를 제안하셨습니다.")
            .url("www.boocam.net/studies/3")
            .isRead(false)
            .receiver(member)
            .build();
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        Thread.sleep(100);
        String eventCacheId3 = memberId + "_" + System.currentTimeMillis();
        Notification notification3 = Notification.builder()
            .type(NotificationType.STUDY_APPROVE)
            .content("nickname4님이 스터디에 참여를 승인하셨습니다.")
            .url("www.boocam.net/studies/4")
            .isRead(false)
            .receiver(member)
            .build();
        emitterRepository.saveEventCache(eventCacheId3, notification3);

        // when
        Map<String, Object> result = emitterRepository.findAllEventCacheByMemberId(
            memberId);

        // then
        Assertions.assertEquals(result.size(), 3);
    }

    @Test
    @DisplayName("Emitter 식별자로 Emitter를 삭제할 수 있다.")
    public void deleteByIdWillSuccess() {
        // given
        long memberId = 1L;
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(emitterId, sseEmitter);

        // when
        emitterRepository.deleteById(emitterId);

        // then
        Assertions.assertEquals(emitterRepository.findAllByMemberId(memberId).size(), 0);
    }

    @Test
    @DisplayName("저장된 모든 Emitter를 제거한다.")
    public void deleteAllByMemberIdWillSuccess() throws InterruptedException {
        // given
        long memberId = 1L;
        String emitterId1 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = memberId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        // when
        emitterRepository.deleteAllByMemberId(memberId);

        // then
        Assertions.assertEquals(emitterRepository.findAllByMemberId(memberId).size(), 0);
    }

    @Test
    @DisplayName("수신한 이벤트 캐시를 모두 삭제할 수 있다.")
    public void deleteAllEventCacheByMemberIdWillSuccess() throws InterruptedException {
        // given
        long memberId = 1L;
        Member member = Member.builder()
            .id(memberId)
            .email("test@mail.com")
            .password("$10$PeUPJ/oN5Kzl..lt1hkB4OVe6L.ESrFa1LHMvBI64TsrYNJU3RXDO")
            .nickname("nickname1")
            .campCrtfc(false)
            .role(Role.ROLE_USER)
            .build();

        String eventCacheId1 = memberId + "_" + System.currentTimeMillis();
        Notification notification1 = Notification.builder()
            .type(NotificationType.STUDY_APPLICATION)
            .content("nickname2님이 스터디에 지원하셨습니다.")
            .url("www.boocam.net/studies/1")
            .isRead(false)
            .receiver(member)
            .build();
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 = memberId + "_" + System.currentTimeMillis();
        Notification notification2 = Notification.builder()
            .type(NotificationType.STUDY_SUGGEST)
            .content("nickname3님이 스터디 참여를 제안하셨습니다.")
            .url("www.boocam.net/studies/3")
            .isRead(false)
            .receiver(member)
            .build();
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        // when
        emitterRepository.deleteAllEventCacheByMemberId(memberId);

        // then
        Assertions.assertEquals(emitterRepository.findAllEventCacheByMemberId(memberId).size(), 0);
    }
}
