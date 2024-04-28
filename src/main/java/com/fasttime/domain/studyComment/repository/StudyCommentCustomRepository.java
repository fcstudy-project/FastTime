package com.fasttime.domain.studyComment.repository;

import com.fasttime.domain.studyComment.dto.request.GetStudyCommentsRequestDTO;
import com.fasttime.domain.studyComment.entity.StudyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyCommentCustomRepository {

    Page<StudyComment> findAllBySearchCondition(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO,
                                                Pageable pageable);

    Long countByStudyID(long studyId);
}
