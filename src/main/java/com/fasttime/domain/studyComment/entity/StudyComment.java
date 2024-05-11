package com.fasttime.domain.studyComment.entity;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.studyComment.dto.response.StudyCommentResponseDTO;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StudyComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "study_id")
    @ManyToOne
    private Study study;

    @JoinColumn(name = "member_Id")
    @ManyToOne
    private Member member;

    private String content;

    @JoinColumn(name = "study_comment_parent_id")
    @ManyToOne
    private StudyComment parentStudyComment;

    @OneToMany(mappedBy = "parentStudyComment")
    private List<StudyComment> childStudyComments = new ArrayList<>();

    @Builder
    public StudyComment(Long id, Study study, Member member, String content,
                        StudyComment parentStudyComment) {
        this.id = id;
        this.study = study;
        this.member = member;
        this.content = content;
        this.parentStudyComment = parentStudyComment;
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
        if (!this.childStudyComments.isEmpty()) {
            this.childStudyComments.forEach(comment -> {
                comment.delete(currentTime);
            });
        }
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public StudyCommentResponseDTO toCommentResponseDTO() {
        boolean isChildComment = this.parentStudyComment != null;
        int deletedChildCommentCount = 0;
        long parentCommentId = isChildComment? this.parentStudyComment.getId() : -1;
        for (StudyComment comment : this.childStudyComments) {
            if (comment.isDeleted()) {
                deletedChildCommentCount++;
            }
        }
        return StudyCommentResponseDTO.builder()
            .commentId(this.id)
            .studyId(this.study.getId())
            .memberId(this.member.getId())
                .parentStudyCommentId(parentCommentId)
            .nickname(this.member.getNickname())
            .content(this.content)
            .childStudyCommentCount(this.childStudyComments.size() - deletedChildCommentCount)
            .createdAt(dateTimeParse(this.getCreatedAt()))
            .updatedAt(dateTimeParse(this.getUpdatedAt()))
            .deletedAt(dateTimeParse(this.getDeletedAt())).build();
    }

    private String dateTimeParse(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
