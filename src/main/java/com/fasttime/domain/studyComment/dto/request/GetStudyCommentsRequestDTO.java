package com.fasttime.domain.studyComment.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetStudyCommentsRequestDTO {

    private final Long studyId;
    private final Long memberId;
    private final Long parentStudyCommentId;

    @Builder
    private GetStudyCommentsRequestDTO(Long studyId, Long memberId, Long parentStudyCommentId) {
        this.studyId = studyId;
        this.memberId = memberId;
        this.parentStudyCommentId = parentStudyCommentId;
    }
}
