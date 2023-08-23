package com.fasttime.domain.comment.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotNull(message = "게시글 ID를 입력하세요.")
    private Long postId;

    @NotNull(message = "회원 ID를 입력하세요.")
    private Long memberId;

    @NotBlank(message = "댓글 내용을 입력하세요.")
    @Size(min = 1, max = 100, message = "내용을 100자 이내로 입력하세요.")
    private String content;

    @NotNull(message = "익명 여부를 선택하세요.")
    private Boolean anonymity;

    private Long parentCommentId;
}