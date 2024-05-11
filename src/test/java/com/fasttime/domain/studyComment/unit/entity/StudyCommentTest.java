package com.fasttime.domain.studyComment.unit.entity;

import com.fasttime.domain.studyComment.entity.StudyComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class StudyCommentTest {

    @DisplayName("댓글을 생성할 수 있다.")
    @Test
    void create_comment_willSuccess() {
        // given
        String content = "test";

        // when
        StudyComment studyComment = StudyComment.builder().study(null).member(null).content(content)
            .parentStudyComment(null).build();

        // then
        assertThat(studyComment).extracting("content","parentStudyComment")
            .containsExactly(content, null);
    }

    @DisplayName("댓글을 수정할 수 있다.")
    @Test
    void update_content_willSuccess() {
        // given
        StudyComment studyComment = StudyComment.builder().study(null).member(null).content("test").
            parentStudyComment(null).build();
        String content = "change";

        // when
        studyComment.updateContent(content);

        // then
        assertThat(studyComment).extracting("content", "parentStudyComment")
            .containsExactly(content, null);
    }

    @DisplayName("댓글을 삭제할 수 있다.")
    @Test
    void delete_comment_willSuccess() {
        // given
        StudyComment studyComment = StudyComment.builder().study(null).member(null).content("test")
            .parentStudyComment(null).build();

        // when
        studyComment.delete(LocalDateTime.now());

        // then
        assertThat(studyComment.getDeletedAt()).isNotNull();
    }
}
