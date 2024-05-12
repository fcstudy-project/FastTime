package com.fasttime.domain.notification.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.notification.entity.Notification;
import com.fasttime.domain.notification.entity.NotificationType;
import com.fasttime.domain.notification.repository.EmitterRepository;
import com.fasttime.domain.notification.repository.NotificationRepository;
import com.fasttime.domain.notification.service.NotificationServiceImpl;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Transactional
@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmitterRepository emitterRepository;

    private final long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    @Test
    @DisplayName("알림 구독을 할 수 있다.")
    public void subscribeWillSuccess() {
        // given
        Member member = Member.builder()
            .id(1L)
            .email("test@mail.com")
            .password("$10$PeUPJ/oN5Kzl..lt1hkB4OVe6L.ESrFa1LHMvBI64TsrYNJU3RXDO")
            .nickname("nickname1")
            .campCrtfc(false)
            .role(Role.ROLE_USER)
            .build();
        String lastEventId = "";

        given(emitterRepository.save(any(String.class), any(SseEmitter.class)))
            .willReturn(new SseEmitter(DEFAULT_TIMEOUT));
        doNothing().when(emitterRepository).deleteById(any(String.class));

        // when then
        Assertions.assertDoesNotThrow(
            () -> notificationService.subscribe(member.getId(), lastEventId));
    }

    @Test
    @DisplayName("알림을 전송할 수 있다.")
    public void sendWillSuccess() {
        // given
        Member member = Member.builder()
            .id(1L)
            .email("test@mail.com")
            .password("$10$PeUPJ/oN5Kzl..lt1hkB4OVe6L.ESrFa1LHMvBI64TsrYNJU3RXDO")
            .nickname("nickname1")
            .campCrtfc(false)
            .role(Role.ROLE_USER)
            .build();
        Notification notification = Notification.builder()
            .id(1L)
            .type(NotificationType.STUDY_APPLICATION)
            .content("nickname2님이 스터디에 지원하셨습니다.")
            .url("www.boocam.net/studies/1")
            .isRead(false)
            .receiver(member)
            .build();
        String emitterId = member.getId() + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
        emitters.put(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        given(notificationRepository.save(any(Notification.class))).willReturn(notification);
        given(emitterRepository.findAllByMemberId(any(Long.TYPE))).willReturn(emitters);
        doNothing().when(emitterRepository)
            .saveEventCache(any(String.class), any(Notification.class));

        // when then
        Assertions.assertDoesNotThrow(
            () -> notificationService.send(
                member,
                NotificationType.STUDY_APPLICATION,
                "nickname2님이 스터디에 지원하셨습니다.",
                "www.boocam.net/studies/1"
            ));
    }
}
