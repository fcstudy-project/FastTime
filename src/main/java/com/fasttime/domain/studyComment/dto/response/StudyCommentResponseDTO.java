package com.fasttime.domain.studyComment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudyCommentResponseDTO {

    private long commentId;
    private long memberId;
    private long studyId;
    private String nickname;
    private String content;
    private long parentStudyCommentId;
    private int childStudyCommentCount;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
