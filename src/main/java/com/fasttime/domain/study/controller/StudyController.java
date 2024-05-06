package com.fasttime.domain.study.controller;

import com.fasttime.domain.study.dto.StudyCreateRequest;
import com.fasttime.domain.study.dto.StudyPageRequestService;
import com.fasttime.domain.study.dto.StudyPageResponse;
import com.fasttime.domain.study.dto.StudyResponse;
import com.fasttime.domain.study.dto.StudyUpdateRequest;
import com.fasttime.domain.study.service.StudyService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDTO<String>> createStudy(
        @RequestBody @Valid StudyCreateRequest request) {
        StudyResponse response = studyService.createStudy(
            securityUtil.getCurrentMemberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.CREATED, "스터디 게시판 생성 성공",
                "/api/v2/studies/%d".formatted(response.id())));
    }

    @PutMapping("/{studyId}")
    public ResponseEntity<ResponseDTO<Long>> updateStudy(
        @PathVariable Long studyId, @RequestBody @Valid StudyUpdateRequest request) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK,
                studyService.updateStudy(studyId, securityUtil.getCurrentMemberId(), request).id()));
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<ResponseDTO<StudyResponse>> deleteStudy(@PathVariable Long studyId) {
        studyService.deleteStudy(studyId,securityUtil.getCurrentMemberId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ResponseDTO.res(HttpStatus.NO_CONTENT, null, null));
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<ResponseDTO<StudyResponse>> getStudy(@PathVariable Long studyId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, studyService.getStudy(studyId)));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<StudyPageResponse>> getStudies(
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "createdAt") String orderBy,
        @RequestParam(defaultValue = "0") int page
    ) {
        StudyPageResponse studyPageResponse = studyService.searchStudies(
            StudyPageRequestService.builder()
                .pageSize(pageSize)
                .page(page)
                .orderBy(orderBy)
                .build().toPageable());
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, studyPageResponse));
    }

}
