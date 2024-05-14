package com.fasttime.domain.study.controller;

import com.fasttime.domain.study.dto.request.ApplyToStudyRequestDto;
import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.dto.request.StudyApplicationPageRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.dto.response.StudyApplicationsResponseDto;
import com.fasttime.domain.study.service.StudyApplicationService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/applications")
    public ResponseEntity<ResponseDTO<StudyApplicationsResponseDto>> getStudyApplications(
        @RequestParam(required = false) Long studyId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                "성공적으로 스터디 참여 신청 목록을 조회했습니다.",
                studyApplicationService.getStudyApplications(
                    GetStudyApplicationsRequestDto.builder()
                        .applicantId(memberId)
                        .studyId(studyId)
                        .build(),
                    StudyApplicationPageRequestDto.builder()
                        .page(page)
                        .size(pageSize)
                        .build().of()
                )
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

    @DeleteMapping("/{studyId}/applications/{studyApplicationId}")
    public ResponseEntity<ResponseDTO<StudyApplicationResponseDto>> rejectStudyApplication(
        @PathVariable @NotNull Long studyApplicationId
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                "성공적으로 스터디 참여 신청을 거부했습니다.",
                studyApplicationService.reject(memberId, studyApplicationId)
            ));
    }
}
