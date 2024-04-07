package com.fasttime.domain.study.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private StudyState studyState;

    private String skill;
    /* 총 인원 수 */
    private int total;
    /* 현재 인원 */
    private int current;
    /* 지원자 수 */
    private int applicant;
    /* 모집 시작 */
    private LocalDate recruitmentStart;
    /* 모집 마감 */
    private LocalDate recruitmentEnd;
    /* 스터디 시작 */
    private LocalDate progressStart;
    /* 스터디 종료 */
    private LocalDate progressEnd;
    /* 연락처 */
    private String contact;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "study_id",cascade = CascadeType.ALL)
    private List<StudyCategory> studyCategories;

    @Builder
    public Study(Long id, String title, String content, String skill, StudyState studyState,
        int total, int current, int applicant, LocalDate recruitmentStart,
        LocalDate recruitmentEnd, LocalDate progressStart,
        LocalDate progressEnd, String contact, Member member, List<StudyCategory> studyCategories) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.studyState = studyState;
        this.skill = skill;
        this.total = total;
        this.current = current;
        this.applicant = applicant;
        this.recruitmentStart = recruitmentStart;
        this.recruitmentEnd = recruitmentEnd;
        this.progressStart = progressStart;
        this.progressEnd = progressEnd;
        this.contact = contact;
        this.member = member;
    }

    public static Study createNewStudy(String title, String content, String skill, int total,
        LocalDate recruitmentEnd, LocalDate progressStart, LocalDate progressEnd,
        String contact, Member member) {
        return Study.builder().title(title)
            .content(content)
            .studyState(StudyState.DURING)
            .skill(skill)
            .total(total)
            .current(0)
            .applicant(0)
            .recruitmentStart(LocalDate.now())
            .recruitmentEnd(recruitmentEnd)
            .progressStart(progressStart)
            .progressEnd(progressEnd)
            .contact(contact)
            .member(member).build();
    }

    public void updateStudy(String title, String content, String skill, int total,
        LocalDate recruitmentEnd, LocalDate progressStart, LocalDate progressEnd, String contact) {
        this.title = title;
        this.content = content;
        this.skill = skill;
        this.total = total;
        this.recruitmentEnd = recruitmentEnd;
        this.progressStart = progressStart;
        this.progressEnd = progressEnd;
        this.contact = contact;

    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }
}
