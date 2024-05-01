package com.fasttime.domain.study.controller;

import com.fasttime.domain.study.dto.request.SuggestStudyRequestDto;
import com.fasttime.domain.study.dto.response.StudyApplicationResponseDto;
import com.fasttime.domain.study.dto.response.StudySuggestionResponseDto;
import com.fasttime.domain.study.service.StudySuggestionService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class StudySuggestionController {

    private final SecurityUtil securityUtil;
    private final StudySuggestionService studySuggestionService;

    @PostMapping("/{studyId}/members/{memberId}")
    public ResponseEntity<ResponseDTO<StudySuggestionResponseDto>> suggestStudy(
        @PathVariable @NotNull Long studyId,
        @PathVariable("memberId") @NotNull Long receiverId,
        @RequestBody SuggestStudyRequestDto suggestStudyRequestDto
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED,
                "성공적으로 스터디 참여를 제안했습니다.",
                studySuggestionService.suggest(
                    memberId,
                    receiverId,
                    studyId,
                    suggestStudyRequestDto
                )
            ));
    }

    @PatchMapping("/{studyId}/suggestions/{studySuggestionId}")
    public ResponseEntity<ResponseDTO<StudySuggestionResponseDto>> approveStudySuggestion(
        @PathVariable @NotNull Long studySuggestionId
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                "성공적으로 스터디 참여 제안을 승인했습니다.",
                studySuggestionService.approve(memberId, studySuggestionId)
            ));
    }

    @DeleteMapping("/{studyId}/suggestions/{studySuggestionId}")
    public ResponseEntity<ResponseDTO<StudySuggestionResponseDto>> rejectStudySuggestion(
        @PathVariable @NotNull Long studySuggestionId
    ) {
        long memberId = securityUtil.getCurrentMemberId();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                "성공적으로 스터디 참여 제안을 거부했습니다.",
                studySuggestionService.reject(memberId, studySuggestionId)
            ));
    }
}
