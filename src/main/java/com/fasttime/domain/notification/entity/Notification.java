package com.fasttime.domain.notification.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 알림 Entity Class
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseTimeEntity {

    /**
     * 알림 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알림 종류: 스터디 참여 지원/제안/승인/거절
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    /**
     * 알림 내용
     */
    @Column(nullable = false)
    private String content;

    /**
     * 알림과 관련된 URL
     */
    private String url;

    /**
     * 알림 확인 여부
     */
    @Column(nullable = false)
    private boolean isRead;

    /**
     * 알림 수신 회원 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiver;

    /**
     * 알림 Entity Builder
     *
     * @param id       알림 식별자
     * @param type     알림 종류
     * @param content  알림 내용
     * @param url      알림과 관련된 URL
     * @param isRead   알림 확인 여부
     * @param receiver 알림 수신 회원 Entity
     */
    @Builder
    public Notification(
        Long id,
        NotificationType type,
        String content,
        String url,
        boolean isRead,
        Member receiver
    ) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.receiver = receiver;
    }
}
