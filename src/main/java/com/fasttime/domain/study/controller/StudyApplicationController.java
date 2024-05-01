package com.fasttime.domain.study.controller;

import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.service.StudyApplicationService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/studies")
@RequiredArgsConstructor
@Validated
public class StudyApplicationController {

    private final SecurityUtil securityUtil;
    private final StudyApplicationService studyApplicationService;

    @PostMapping("/{studyId}")
    public ResponseEntity<ResponseDTO<StudyApplicationResponseDto>> applyToStudy(
        @PathVariable @NotNull Long studyId,
        @RequestBody ApplyToStudyRequestDto applyToStudyRequestDto
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED,
                "성공적으로 스터디 참여를 신청했습니다.",
                studyApplicationService.apply(memberId, studyId, applyToStudyRequestDto)
            ));
    }

    @PatchMapping("/{studyId}/applications/{studyApplicationId}")
    public ResponseEntity<ResponseDTO<StudyApplicationResponseDto>> approveStudyApplication(
        @PathVariable @NotNull Long studyApplicationId
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                "성공적으로 스터디 참여 신청을 승인했습니다.",
                studyApplicationService.approve(memberId, studyApplicationId)
            ));
    }
}