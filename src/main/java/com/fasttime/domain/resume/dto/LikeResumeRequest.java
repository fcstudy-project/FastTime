package com.fasttime.domain.resume.dto;

public record LikeResumeRequest(
        Long resumeId,
        Long memberId
) {

}
