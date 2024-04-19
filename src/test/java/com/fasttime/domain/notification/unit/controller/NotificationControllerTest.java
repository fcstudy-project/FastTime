package com.fasttime.domain.notification.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasttime.util.ControllerUnitTestSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class NotificationControllerTest extends ControllerUnitTestSupporter {

    @Nested
    @DisplayName("subscribe()은")
    class Context_subscribe {

        @Test
        @DisplayName("알림을 구독할 수 있다.")
        void _willSuccess() throws Exception {
            // given
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("MEMBER", 1L);
            given(notificationService.subscribe(any(long.class), any(String.class)))
                .willReturn(new SseEmitter(60L * 1000L * 60L));

            // when, then
            mockMvc.perform(get("/api/v2/notifications/subscribe")
                    .session(session)
                    .contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andDo(print());

            verify(notificationService, times(1)).subscribe(any(long.class), any(String.class));
        }
    }
}
