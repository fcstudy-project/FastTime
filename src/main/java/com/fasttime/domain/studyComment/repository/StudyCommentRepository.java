package com.fasttime.domain.studyComment.repository;

import com.fasttime.domain.studyComment.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long>, StudyCommentCustomRepository {

}