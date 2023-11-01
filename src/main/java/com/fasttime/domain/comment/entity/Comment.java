package com.fasttime.domain.comment.entity;

import com.fasttime.domain.comment.dto.response.MyPageCommentResponseDTO;
import com.fasttime.domain.comment.dto.response.PostCommentResponseDTO;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.article.entity.Article;
import com.fasttime.global.common.BaseTimeEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "article_id")
    @ManyToOne
    private Article article;

    @JoinColumn(name = "member_Id")
    @ManyToOne
    private Member member;

    private String content;

    private boolean anonymity;

    @JoinColumn(name = "comment_parent_id")
    @ManyToOne
    private Comment parentComment;

    @Builder
    public Comment(Long id, Article article, Member member, String content, boolean anonymity,
        Comment parentComment) {
        this.id = id;
        this.article = article;
        this.member = member;
        this.content = content;
        this.anonymity = anonymity;
        this.parentComment = parentComment;
    }

    @Override
    public void delete(LocalDateTime currentTime) {
        super.delete(currentTime);
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public PostCommentResponseDTO toPostCommentResponseDTO() {
        Long parentCommentId = null;
        if (this.parentComment != null) {
            parentCommentId = this.parentComment.getId();
        }
        return PostCommentResponseDTO.builder().id(this.id).memberId(this.member.getId())
            .nickname(this.member.getNickname()).content(this.content).anonymity(this.anonymity)
            .parentCommentId(parentCommentId).createdAt(dateTimeParse(this.getCreatedAt()))
            .updatedAt(dateTimeParse(this.getUpdatedAt()))
            .deletedAt(dateTimeParse(this.getDeletedAt())).build();
    }

    public MyPageCommentResponseDTO toMyPageCommentResponseDTO() {
        Long parentCommentId = null;
        if (this.parentComment != null) {
            parentCommentId = this.parentComment.getId();
        }
        return MyPageCommentResponseDTO.builder().id(this.id).postId(this.article.getId())
            .nickname(this.member.getNickname()).content(this.content).anonymity(this.anonymity)
            .parentCommentId(parentCommentId).createdAt(dateTimeParse(this.getCreatedAt()))
            .updatedAt(dateTimeParse(this.getUpdatedAt()))
            .deletedAt(dateTimeParse(this.getDeletedAt())).build();
    }

    private String dateTimeParse(LocalDateTime dateTime) {
        return (dateTime != null) ? dateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
