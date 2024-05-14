package com.fasttime.domain.study.entity;

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
 * 스터디 참여 신청 Entity
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StudyApplication extends BaseTimeEntity {

    /**
     * 스터디 참여 신청 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 스터디 참여 신청 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyRequestStatus status;

    /**
     * 스터디 참여를 신청한 회원 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member applicant;

    /**
     * 참여 신청한 스터디 Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Study study;

    /**
     * 스터디 참여 신청 메시지
     */
    private String message;

    /**
     * 스터디 참여 신청 생성자 + 빌더
     *
     * @param id        스터디 참여 신청 식별자
     * @param status    스터디 참여 신청 상태
     * @param applicant 스터디 참여를 신청한 회원 Entity
     * @param study     참여 신청한 스터디 Entity
     * @param message   스터디 참여 신청 메시지
     */
    @Builder
    public StudyApplication(
        Long id,
        StudyRequestStatus status,
        Member applicant,
        Study study,
        String message
    ) {
        this.id = id;
        this.status = status;
        this.applicant = applicant;
        this.study = study;
        this.message = message;
    }

    /**
     * 스터디 참여 신청의 상태를 변경하는 메서드
     *
     * @param newStatus 변경하고자 하는 스터디 참여 신청 상태
     */
    public void changeStatus(StudyRequestStatus newStatus) {
        this.status = newStatus;
    }
}
