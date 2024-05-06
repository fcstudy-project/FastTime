package com.fasttime.domain.notification.unit.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.entity.Role;
import com.fasttime.domain.notification.entity.Notification;
import com.fasttime.domain.notification.entity.NotificationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NotificationTest {

    @Test
    @DisplayName("알림을 생성할 수 있다.")
    public void createNotificationWillSuccess(){
        // given
        Member member = Member.builder()
            .id(1L)
            .email("test@mail.com")
            .password("$10$PeUPJ/oN5Kzl..lt1hkB4OVe6L.ESrFa1LHMvBI64TsrYNJU3RXDO")
            .nickname("nickname")
            .campCrtfc(false)
            .role(Role.ROLE_USER)
            .build();

        // when
        String content = "nickname님이 스터디에 지원하셨습니다.";
        String url = "www.boocam.net/studies/1";
        boolean isRead = false;

        // then
        Assertions.assertDoesNotThrow(() -> Notification.builder()
            .type(NotificationType.STUDY_APPLICATION)
            .content(content)
            .url(url)
            .isRead(isRead)
            .receiver(member)
            .build()
        );
    }
}
