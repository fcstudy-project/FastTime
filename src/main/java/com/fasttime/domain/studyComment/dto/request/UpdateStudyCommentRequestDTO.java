package com.fasttime.domain.studyComment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudyCommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력하세요.") @Size(min = 1, max = 100, message = "내용을 100자 이내로 입력하세요.") String content;
}
