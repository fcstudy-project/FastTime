package com.fasttime.domain.notification.dto.response;

import com.fasttime.domain.notification.entity.Notification;
import lombok.Builder;

@Builder
public record NotificationResponseDto(
    long id,
    String type,
    String nickname,
    String content,
    String url
) {

    public static NotificationResponseDto of(Notification notification) {
        return NotificationResponseDto.builder()
            .id(notification.getId())
            .type(notification.getType().name())
            .nickname(notification.getReceiver().getNickname())
            .content(notification.getContent())
            .url(notification.getUrl())
            .build();
    }
}
