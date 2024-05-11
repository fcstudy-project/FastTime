package com.fasttime.domain.studyComment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StudyCommentListResponseDTO {

    private int totalPages;
    private Boolean isLastPage;
    private long totalComments;
    private List<StudyCommentResponseDTO> comments;
}
